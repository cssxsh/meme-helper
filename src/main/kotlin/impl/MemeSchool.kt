package xyz.cssxsh.mirai.meme.impl

import org.jetbrains.skia.Image as SkImage
import net.mamoe.mirai.console.permission.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import org.jetbrains.skia.*
import org.jetbrains.skia.paragraph.*
import xyz.cssxsh.mirai.meme.service.*
import xyz.cssxsh.mirai.skia.*
import xyz.cssxsh.skia.*
import java.io.File
import java.util.*

public class MemeSchool : MemeService {
    override val name: String = "学历"
    override val id: String = "school"
    override val description: String = ""
    override val loaded: Boolean = true
    override val properties: Properties = Properties()
    override lateinit var permission: Permission
    override val regex: Regex = """^#学历""".toRegex()

    private val background by lazy {
        SkImage.makeFromEncoded(this::class.java.getResource("record.png")!!.readBytes())
    }

    override suspend fun MessageEvent.replier(match: MatchResult): Message {
        val surface = Surface.makeRaster(background.imageInfo)
        val canvas = surface.canvas
        canvas.drawImage(background, 0F, 0F)

        val fonts = FontCollection()
            .setDynamicFontManager(FontUtils.provider)
            .setDefaultFontManager(FontMgr.default, "黑体")
        val style = ParagraphStyle().apply {
            direction = Direction.RTL
        }
        message.contentToString()
            .removePrefix("#学历\n")
            .lines()
            .forEachIndexed { index, line ->
                val paragraph = ParagraphBuilder(style, fonts)
                    .pushStyle(
                        TextStyle()
                        .setFontSize(42F)
                        .setColor(0xFF979797u.toInt()))
                    .addText(line)
                    .build()
                    .layout(1000F)
                paragraph.paint(canvas, 10F, 450F + index * 138)
            }
        return surface.makeSnapshotResource().use { subject.uploadImage(it) }
    }

    override fun load(folder: File) {}

    override fun enable(permission: Permission) {
        this.permission = permission
    }

    override fun disable() {}
}