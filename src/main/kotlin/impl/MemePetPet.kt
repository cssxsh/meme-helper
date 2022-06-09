package xyz.cssxsh.mirai.meme.impl

import kotlinx.coroutines.*
import net.mamoe.mirai.console.permission.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.mirai.meme.*
import xyz.cssxsh.mirai.meme.service.*
import xyz.cssxsh.mirai.skia.toImageResource
import xyz.cssxsh.skia.*
import java.io.File
import java.util.*

/**
 * [PetPet](https://benisland.neocities.org/petpet)
 */
public class MemePetPet : MemeService {
    override val name: String = "PetPet Meme"
    override val id: String = "petpet"
    override val description: String = "摸摸头 生成器"
    override var loaded: Boolean = false
        private set
    override var regex: Regex = """^#pet\s*(\d+)?""".toRegex()
        private set
    override val properties: Properties = Properties().apply { put("regex", regex.pattern) }
    override var permission: Permission = Permission.getRootPermission()
        private set

    override fun load(folder: File, permission: Permission) {
        this.permission = permission
        when (val re = properties["regex"]) {
            is String -> regex = re.toRegex()
            is Regex -> regex = re
            else -> {}
        }
        MemeService.launch {
            folder.mkdirs()

            val sprite = folder.resolve("sprite.png")
            if (sprite.exists().not()) {
                download(urlString = "https://benisland.neocities.org/petpet/img/sprite.png", folder)
                    .renameTo(sprite)
            }
            System.setProperty(PET_PET_SPRITE, sprite.absolutePath)

            loaded = true
        }
    }

    override fun enable() {}

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