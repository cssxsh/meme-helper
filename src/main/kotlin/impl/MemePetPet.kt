package xyz.cssxsh.mirai.meme.impl

import kotlinx.coroutines.*
import net.mamoe.mirai.console.permission.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.mirai.meme.*
import xyz.cssxsh.mirai.meme.service.*
import xyz.cssxsh.mirai.skia.*
import xyz.cssxsh.skia.*
import java.io.*
import java.util.*

/**
 * [PetPet](https://benisland.neocities.org/petpet)
 */
public class MemePetPet : MemeService {
    override val name: String = "PetPet Meme"
    override val id: String = "petpet"
    override val description: String = "摸摸头 生成器"
    override val loaded: Boolean = true
    override var regex: Regex = """^#pet\s*(\d+)?""".toRegex()
        private set
    override val properties: Properties = Properties().apply { put("regex", regex.pattern) }
    override lateinit var permission: Permission
    private lateinit var loadJob: Job

    override fun load(folder: File) {
        when (val re = properties["regex"]) {
            is String -> regex = re.toRegex()
            is Regex -> regex = re
            else -> {}
        }
        loadJob = MemeService.launch {
            folder.mkdirs()

            val sprite = folder.resolve("sprite.png")
            if (sprite.exists().not()) {
                download(urlString = "https://benisland.neocities.org/petpet/img/sprite.png", folder)
                    .renameTo(sprite)
            }
            System.setProperty(PET_PET_SPRITE, sprite.absolutePath)
        }
    }

    override fun enable(permission: Permission) {
        this.permission = permission
        runBlocking {
            loadJob.join()
        }
    }

    override fun disable() {}

    override suspend fun MessageEvent.replier(match: MatchResult): Image {
        val id = match.groups[1]?.value?.toLongOrNull()
            ?: message.findIsInstance<At>()?.target
            ?: sender.id
        val image = message.findIsInstance<Image>()
        val face = when {
            image != null -> cache(image = image)
            else -> avatar(id = id)
        }

        return petpet(face).toImageResource(formatName = "gif")
            .use { resource -> subject.uploadImage(resource = resource) }
    }
}