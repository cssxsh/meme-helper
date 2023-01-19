package xyz.cssxsh.mirai.meme.impl

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.jvm.javaio.*
import net.mamoe.mirai.console.permission.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.isUploaded
import net.mamoe.mirai.utils.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import xyz.cssxsh.mirai.hibernate.*
import xyz.cssxsh.mirai.hibernate.entry.*
import xyz.cssxsh.mirai.meme.*
import xyz.cssxsh.mirai.meme.service.*
import java.io.*
import java.util.*

public class MemeRecord : MemeService {
    override val name: String = "Face Record"
    override val id: String = "record"
    override val description: String = "从群聊记录里获得表情包"
    override var loaded: Boolean = true
    override var regex: Regex = """^#群友表情\s*(.*)|#([^#\s]+)#""".toRegex()
        private set
    override val properties: Properties = Properties().apply { put("regex", regex.pattern) }
    override lateinit var permission: Permission
    private var folder: File = File(System.getProperty("user.dir", ".")).resolve(".record")

    private suspend fun FaceRecord.download(): File {
        return folder.listFiles { file -> file.name.startsWith(md5) }?.firstOrNull()
            ?: http.prepareGet(url).execute { response ->
                val type = response.contentType()?.contentSubtype ?: "mirai"

                val file = folder.resolve("$md5.$type")

                logger.info { "文件 ${file.name} 开始下载" }
                file.outputStream().use { output ->
                    val channel = response.bodyAsChannel()

                    while (!channel.isClosedForRead) channel.copyTo(output)
                }

                file
            }
    }

    override fun load(folder: File) {
        this.folder = folder
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

    override suspend fun MessageEvent.replier(match: MatchResult): MessageContent? {
        val tag = match.groupValues.last { it.isNotEmpty() }
        val record = if (tag.startsWith('#')) {
            FaceRecord.random()
        } else {
            FaceRecord.match(tag = tag).randomOrNull()
                ?: MiraiHibernateRecorder.face(md5 = tag)
                ?: return null
        }
        val message = record.toMessageContent()
        if (message is Image && message.isUploaded(bot).not()) {
            return record.download().uploadAsImage(subject)
        }
        return message
    }
}