package xyz.cssxsh.mirai.meme.impl

import net.mamoe.mirai.console.permission.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.isUploaded
import xyz.cssxsh.mirai.hibernate.*
import xyz.cssxsh.mirai.hibernate.entry.*
import xyz.cssxsh.mirai.meme.service.*
import java.io.File
import java.util.*

public class MemeRecord : MemeService {
    override val name: String = "Face Record"
    override val id: String = "record"
    override val description: String = "从群聊记录里获得表情包"
    override var loaded: Boolean = true
    override var regex: Regex = """^#群友表情""".toRegex()
        private set
    override val properties: Properties = Properties().apply { put("regex", regex.pattern) }
    override lateinit var permission: Permission

    override fun load(folder: File) {
        when (val re = properties["regex"]) {
            is String -> regex = re.toRegex()
            is Regex -> regex = re
            else -> {}
        }
        loaded = try {
            MiraiHibernateRecorder
            true
        } catch (_: Throwable) {
            false
        }
    }

    override fun enable(permission: Permission) {
        this.permission = permission
    }

    override fun disable() {}

    override suspend fun MessageEvent.replier(match: MatchResult): MessageContent {
        var i = 3
        while (i-- > 0) {
            val message = FaceRecord.random().toMessageContent()
            if (message is Image && message.isUploaded(bot).not()) continue
            return message
        }
        return FaceRecord.random().toMessageContent()
    }
}