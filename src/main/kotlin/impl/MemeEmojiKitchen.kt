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
import java.io.*
import java.time.*
import java.util.*
import java.util.zip.*
import kotlin.collections.*

/**
 * [Emoji Kitchen](https://github.com/xsalazar/emoji-kitchen/)
 */
public class MemeEmojiKitchen : MemeService {
    override val name: String = "Emoji Kitchen"
    override val id: String = "emoji"
    override val description: String = "Emoji 合成"
    override val loaded: Boolean = true
    override val regex: Regex = EmojiKitchen.EMOJI_REGEX.toRegex()
    override val properties: Properties = Properties()
    override lateinit var permission: Permission
    private var kitchen: EmojiKitchen = EmojiKitchen(items = emptyMap())
    private var folder: File = File(System.getProperty("user.dir", ".")).resolve(".emoji")
    private lateinit var loadJob: Job

    override fun load(folder: File) {
        this.folder = folder
        loadJob = MemeService.launch(CoroutineName(name)) {
            folder.mkdirs()

            val archive = folder.resolve("emoji-kitchen-main.zip")
            if (OffsetDateTime.parse(EmojiKitchen.LAST_UPDATE).toInstant().toEpochMilli() > archive.lastModified()) {
                archive.delete()
            }
            if (archive.exists().not()) {
                try {
                    download(
                        urlString = "https://mirror.ghproxy.com/https://github.com/xsalazar/emoji-kitchen-backend/archive/main.zip",
                        folder = folder
                    )
                } catch (_: Exception) {
                    archive.delete()
                    download(
                        urlString = "https://github.com/xsalazar/emoji-kitchen-backend/archive/main.zip",
                        folder = folder
                    )
                }.renameTo(archive)
            }
            val metadata = ZipFile(archive).use { zip ->
                val entry = zip.getEntry("emoji-kitchen-backend-main/app/metadata.json")
                @OptIn(ExperimentalSerializationApi::class)
                Json.decodeFromStream<EmojiKitchenMetadata>(stream = zip.getInputStream(entry))
            }
            kitchen = EmojiKitchen(items = metadata.data)
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