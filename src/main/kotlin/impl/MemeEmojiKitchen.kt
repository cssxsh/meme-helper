package xyz.cssxsh.mirai.meme.impl

import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import net.mamoe.mirai.console.permission.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import xyz.cssxsh.mirai.meme.*
import xyz.cssxsh.mirai.meme.service.*
import java.io.File
import java.time.*
import java.util.*
import kotlin.collections.*

/**
 * [Google Sticker Mashup Research](https://github.com/UCYT5040/Google-Sticker-Mashup-Research)
 */
public class MemeEmojiKitchen : MemeService {
    override val name: String = "Emoji Kitchen"
    override val id: String = "emoji"
    override val description: String = "Emoji 合成"
    override val loaded: Boolean = true
    override val regex: Regex = EmojiKitchen.EMOJI_REGEX.toRegex()
    override val properties: Properties = Properties()
    override lateinit var permission: Permission
    private var kitchen: EmojiKitchen = EmojiKitchen(urls = emptyMap())
    private var folder: File = File(System.getProperty("user.dir", ".")).resolve(".emoji")
    private lateinit var loadJob: Job

    override fun load(folder: File) {
        this.folder = folder
        loadJob = MemeService.launch {
            folder.mkdirs()

            val data = folder.resolve("image_urls.json")
            if (OffsetDateTime.parse(EmojiKitchen.LAST_UPDATE).toInstant().toEpochMilli() > data.lastModified()) {
                data.delete()
            }
            if (data.exists().not()) {
                try {
                    download(
                        urlString = "https://github.com/UCYT5040/Google-Sticker-Mashup-Research/raw/main/image_urls.json",
                        folder = folder
                    )
                } catch (_: Exception) {
                    data.delete()
                    download(
                        urlString = "https://raw.fastgit.org/UCYT5040/Google-Sticker-Mashup-Research/main/image_urls.json",
                        folder = folder
                    )
                }.renameTo(data)
            }
            kitchen = EmojiKitchen(urls = Json.decodeFromString<HashMap<String, String>>(data.readText()))
        }
    }

    override fun enable(permission: Permission) {
        this.permission = permission
        runBlocking {
            loadJob.join()
        }
    }

    override fun disable() {}

    override suspend fun MessageEvent.replier(match: MatchResult): Message? {
        val first = match.value
        val second = match.next()?.value ?: return null
        val (filename, url) = kitchen.cook(first, second)
            ?: kitchen.cook(second, first)
            ?: return null

        return (folder.resolve(filename).takeIf { it.exists() } ?: download(urlString = url, folder = folder))
            .uploadAsImage(contact = subject)
    }
}