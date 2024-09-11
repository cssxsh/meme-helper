package xyz.cssxsh.mirai.meme.impl

import kotlinx.coroutines.*
import net.mamoe.mirai.console.permission.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.mirai.meme.*
import xyz.cssxsh.mirai.meme.service.*
import xyz.cssxsh.mirai.skia.*
import java.io.*
import java.util.*
import kotlin.collections.*

/**
 * [Project Sekai Stickers](https://st.ayaka.one/)
 */
public class MemeProjectSekaiStickers : MemeService {
    override val name: String = "Project Sekai Stickers"
    override val id: String = "sekai-stickers"
    override val description: String = "Stickers 合成"
    override val loaded: Boolean = true
    override lateinit var regex: Regex
    override val properties: Properties = Properties()
    override lateinit var permission: Permission
    private lateinit var tool: ProjectSekaiStickers
    private var folder: File = File(System.getProperty("user.dir", ".")).resolve(".sekai-stickers")
    private lateinit var loadJob: Job

    override fun load(folder: File) {
        this.folder = folder
        loadJob = MemeService.launch(CoroutineName(name)) {
            folder.mkdirs()

            val archive = folder.resolve("sekai-stickers-main.zip")
            if (archive.exists().not()) {
                try {
                    download(
                        urlString = "https://mirror.ghproxy.com/https://github.com/TheOriginalAyaka/sekai-stickers/archive/main.zip",
                        folder = folder
                    )
                } catch (_: Exception) {
                    archive.delete()
                    download(
                        urlString = "https://github.com/TheOriginalAyaka/sekai-stickers/archive/main.zip",
                        folder = folder
                    )
                }.renameTo(archive)
            }
            tool = ProjectSekaiStickers(file = archive)
            regex = tool.characters
                .joinToString(prefix = "#(", separator = "|", postfix = ")\\s*(.*)") { it.name }
                .toRegex()
        }
    }

    override fun enable(permission: Permission) {
        this.permission = permission
        runBlocking {
            loadJob.join()
        }
    }

    override fun disable() {}

    override suspend fun MessageEvent.replier(match: MatchResult): Message {
        val (name, text) = match.destructured
        val image = tool.create(name = name) {
            if (text.isNotBlank()) this.text = text
        }

        return image.makeSnapshotResource()
            .use { resource -> subject.uploadImage(resource = resource) }
    }
}