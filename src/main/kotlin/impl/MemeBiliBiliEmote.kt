package xyz.cssxsh.mirai.meme.impl

import net.mamoe.mirai.console.permission.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import xyz.cssxsh.mirai.bilibili.data.*
import xyz.cssxsh.mirai.meme.*
import xyz.cssxsh.mirai.meme.service.*
import java.io.File
import java.util.*

public class MemeBiliBiliEmote : MemeService {
    override val name: String = "BiliBili Emote"
    override val id: String = "bilibili"
    override val description: String = "获取B站表情包"
    override var loaded: Boolean = true
    override var regex: Regex = """^#B表情""".toRegex()
        private set
    override val properties: Properties = Properties().apply { put("regex", regex.pattern) }
    override lateinit var permission: Permission
    private var folder: File = File(System.getProperty("user.dir") ?: ".").resolve(".bilibili")

    override fun load(folder: File) {
        this.folder = folder
        when (val re = properties["regex"]) {
            is String -> regex = re.toRegex()
            is Regex -> regex = re
            else -> {}
        }
        loaded = try {
            BiliEmoteData
            true
        } catch (_: Throwable) {
            false
        }

    }

    override fun enable(permission: Permission) {
        this.permission = permission
    }

    override fun disable() {}

    override suspend fun MessageEvent.replier(match: MatchResult): MessageChain? {
        val content = message.content
        val emotes = buildList {
            for ((text, item) in BiliEmoteData.dynamic) {
                if ("<$text>" in content) {
                    addAll(item.emote)
                    continue
                }
                for (emote in item.emote) {
                    if (emote.text in content) add(emote)
                }
            }
        }
        if (emotes.isEmpty()) return null
        return buildMessageChain {
            for (emote in emotes) {
                val file = folder.resolve(emote.url.substringAfterLast('/')).takeIf { it.exists() }
                        ?: download(urlString = emote.url, folder = folder)
                +file.uploadAsImage(contact = subject)
            }
        }
    }
}