package xyz.cssxsh.mirai.meme.impl

import kotlinx.coroutines.*
import net.mamoe.mirai.console.permission.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.mirai.meme.download
import xyz.cssxsh.mirai.meme.service.*
import xyz.cssxsh.mirai.skia.makeSnapshotResource
import xyz.cssxsh.skia.*
import java.io.File
import java.util.*

public class MemeZZKIA : MemeService {
    override val name: String = "ZZKIA Pinyin"
    override val id: String = "zzkia"
    override val description: String = "ZZKIA Pinyin 生成器"
    override var loaded: Boolean = false
        private set
    override var regex: Regex = """^#(zzkia|pinyin)\s+((?s).+)""".toRegex()
        private set
    override val properties: Properties = Properties().apply { put("regex", regex.pattern) }
    override lateinit var permission: Permission
        private set

    override fun load(folder: File, permission: Permission) {
        this.permission = permission
        when (val re = properties["regex"]) {
            is String -> regex = re.toRegex()
            is Regex -> regex = re
            else -> {}
        }
        MemeService.launch {
            val zzkia = folder.resolve("zzkia.jpg")
            if (zzkia.exists().not()) {
                try {
                   download(urlString = "https://github.com/dcalsky/bbq/raw/master/zzkia/images/4.jpg", folder)
                } catch (_: Exception) {
                    download(urlString = "https://tvax2.sinaimg.cn/large/d6ca1528gy1h2ur64giqmj20yi0v3q52.jpg", folder)
                }.renameTo(zzkia)
            }
            System.setProperty(ZZKIA_ORIGIN, zzkia.absolutePath)
            val fzxs14 = folder.resolve("FZXS14-ex.ttf")
            if (fzxs14.exists().not()) {
                try {
                    download(urlString = "https://github.com/dcalsky/bbq/raw/master/fonts/FZXS14-ex.ttf", folder)
                } catch (_: Exception) {
                    download(urlString = "https://font.taofont.com/en_fonts/fonts/f/FZXS14.ttf", folder)
                }.renameTo(fzxs14)
            }
            FontUtils.loadTypeface(path = fzxs14.path)

            loaded = true
        }
    }

    override fun enable() {}

    override fun disable() {}

    override suspend fun MessageEvent.replier(match: MatchResult): Image {
        val (_, text) = match.destructured

        return zzkia(text).makeSnapshotResource()
            .use { resource -> subject.uploadImage(resource = resource) }
    }
}