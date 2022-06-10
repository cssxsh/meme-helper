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
import java.util.*
import kotlin.collections.*

/**
 * [Google Sticker Mashup Research](https://github.com/UCYT5040/Google-Sticker-Mashup-Research)
 */
public class MemeEmojiKitchen : MemeService {
    override val name: String = "Emoji Kitchen"
    override val id: String = "emoji"
    override val description: String = "Emoji 合成"
    override var loaded: Boolean = false
        private set
    override val regex: Regex = EmojiKitchen.REGEX.toRegex()
    override val properties: Properties = Properties()
    override var permission: Permission = Permission.getRootPermission()
        private set
    private var kitchen: EmojiKitchen = EmojiKitchen(urls = emptyMap())
    private var folder: File = File(System.getProperty("user.dir") ?: ".").resolve(".emoji")

    override fun load(folder: File, permission: Permission) {
        this.folder = folder
        this.permission = permission
        MemeService.launch {
            folder.mkdirs()

            val data = folder.resolve("image_urls.json")
            if (data.exists().not()) {
                try {
                    download(
                        urlString = "https://github.com/UCYT5040/Google-Sticker-Mashup-Research/raw/main/image_urls.json",
                        folder
                    )
                } catch (_: Exception) {
                    download(
                        urlString = "https://raw.fastgit.org/UCYT5040/Google-Sticker-Mashup-Research/main/image_urls.json",
                        folder
                    )
                }.renameTo(data)
            }
            kitchen = data.inputStream().use { stream ->
                @OptIn(ExperimentalSerializationApi::class)
                EmojiKitchen(urls = Json.decodeFromStream<HashMap<String, String>>(stream))
            }

            loaded = true
        }
    }

    override fun enable() {}

    override fun disable() {}

    override suspend fun MessageEvent.replier(match: MatchResult): Message? {
        val (filename, url) = kitchen
            .cook(emojis = generateSequence(match, MatchResult::next).map { it.value })
            ?: return null

        return (folder.resolve(filename).takeIf { it.exists() } ?: download(urlString = url, folder = folder))
            .uploadAsImage(contact = subject)
    }
}