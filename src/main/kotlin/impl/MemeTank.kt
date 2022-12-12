package xyz.cssxsh.mirai.meme.impl

import net.mamoe.mirai.console.permission.*
import net.mamoe.mirai.console.util.SemVersion
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.mirai.meme.cache
import xyz.cssxsh.mirai.meme.service.*
import xyz.cssxsh.mirai.skia.*
import xyz.cssxsh.skia.*
import java.io.*
import java.util.*

/**
 * [幻影坦克](https://samarium150.github.io/mirage-tank-images/)
 */
public class MemeTank : MemeService {
    override val name: String = "幻影坦克"
    override val id: String = "tank"
    override val description: String = "幻影坦克 生成器"
    override var loaded: Boolean = false
    override var regex: Regex = """^#tank""".toRegex()
        private set
    override val properties: Properties = Properties().apply { put("regex", regex.pattern) }
    override lateinit var permission: Permission

    override fun load(folder: File) {
        when (val re = properties["regex"]) {
            is String -> regex = re.toRegex()
            is Regex -> regex = re
            else -> {}
        }

        loaded = SemVersion.parseRangeRequirement(">= 0.7.32")
            .test(SemVersion(org.jetbrains.skiko.Version.skiko))
    }

    override fun enable(permission: Permission) {
        this.permission = permission
    }

    override fun disable() {}

    override suspend fun MessageEvent.replier(match: MatchResult): Image? {
        val images = message.filterIsInstance<Image>()
        if (images.size < 2) return null
        val top = cache(image = images[0])
        val bottom = cache(image = images[1])

        return tank(top, bottom).makeSnapshotResource()
            .use { resource -> subject.uploadImage(resource = resource) }
    }
}