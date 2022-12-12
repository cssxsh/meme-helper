package xyz.cssxsh.mirai.meme.impl

import net.mamoe.mirai.console.permission.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.mirai.meme.service.*
import xyz.cssxsh.mirai.skia.*
import xyz.cssxsh.skia.*
import java.io.*
import java.util.*

/**
 * [5000choyen](https://github.com/yurafuca/5000choyen)
 */
public class MemeChoYen : MemeService {
    override val name: String = "5000兆円"
    override val id: String = "choyen"
    override val description: String = "5000兆円 生成器"
    override val loaded: Boolean = true
    override var regex: Regex = """^#choyen\s+(\S+)\s+(\S+)""".toRegex()
        private set
    override val properties: Properties = Properties().apply { put("regex", regex.pattern) }
    override lateinit var permission: Permission

    override fun load(folder: File) {
        when (val re = properties["regex"]) {
            is String -> regex = re.toRegex()
            is Regex -> regex = re
            else -> {}
        }
    }

    override fun enable(permission: Permission) {
        this.permission = permission
    }

    override fun disable() {}

    override suspend fun MessageEvent.replier(match: MatchResult): Image {
        val (top, bottom) = match.destructured

        return choyen(top, bottom).makeSnapshotResource()
            .use { resource -> subject.uploadImage(resource = resource) }
    }
}