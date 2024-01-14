package xyz.cssxsh.mirai.meme

import org.jetbrains.skia.*
import org.jetbrains.skia.paragraph.*
import xyz.cssxsh.skia.*
import java.io.*
import java.util.*

/**
 * [游戏王制卡器](https://ymssx.github.io/ygo/#/)
 */
public sealed class YgoCard {

    public abstract val name: String

    public abstract val color: Int

    public abstract val attribute: Attribute

    public abstract val type: String

    public abstract val face: Image

    public abstract val race: List<String>

    public abstract val description: String

    public abstract val locale: Locale

    public abstract val copyright: String?

    protected abstract fun frame(folder: File): File

    @Suppress("EnumEntryName")
    public enum class Attribute {
        dark, divine, earth, fire, light, spell, trap, water, wind;

        public companion object {
            public val monster: List<Attribute> by lazy { values().toList() - spell - trap }
        }
    }

    /**
     * 怪兽卡
     */
    public data class Monster(
        override var name: String,
        override var face: Image,
        override var description: String,
        val level: Int = 1,
        val attack: String = "0",
        val defend: String = "0",
        override val race: List<String>,
        override var attribute: Attribute,
        override var color: Int = Color.BLACK,
        override var locale: Locale = Locale.getDefault(),
        override var copyright: String? = null
    ) : YgoCard() {
        override val type: String = ""
        override fun frame(folder: File): File = folder.resolve("source/mold/frame/monster_xg.jpg")
    }

    /**
     * 魔法卡
     */
    public data class Spell(
        override var name: String,
        override var face: Image,
        override var description: String,
        override var color: Int = Color.BLACK,
        override var locale: Locale = Locale.getDefault(),
        override var copyright: String? = null
    ) : YgoCard() {
        override val race: List<String> = emptyList()
        override val attribute: Attribute = Attribute.spell
        override val type: String = "【" + when (locale) {
            Locale.JAPAN -> "魔法卡"
            Locale.ENGLISH -> "魔法卡"
            Locale.SIMPLIFIED_CHINESE -> "魔法卡"
            Locale.TRADITIONAL_CHINESE -> "魔法卡"
            else -> "魔法卡"
        } + "】"
        override fun frame(folder: File): File = folder.resolve("source/mold/frame/spell.jpg")
    }

    /**
     * 陷阱卡
     */
    public data class Trap(
        override var name: String,
        override var face: Image,
        override var description: String,
        override var color: Int = Color.BLACK,
        override var locale: Locale = Locale.getDefault(),
        override var copyright: String? = null
    ) : YgoCard() {
        override val race: List<String> = emptyList()
        override val attribute: Attribute = Attribute.trap
        override val type: String = "【" + when (locale) {
            Locale.JAPAN -> "陷阱卡"
            Locale.ENGLISH -> "陷阱卡"
            Locale.SIMPLIFIED_CHINESE -> "陷阱卡"
            Locale.TRADITIONAL_CHINESE -> "陷阱卡"
            else -> "陷阱卡"
        } + "】"
        override fun frame(folder: File): File = folder.resolve("source/mold/frame/trap.jpg")

    }

    private val fonts = FontCollection()
        .setDynamicFontManager(FontUtils.provider)
        .setDefaultFontManager(FontMgr.default)

    private fun name(): Paragraph {
        val style = ParagraphStyle().apply {
            maxLinesCount = 1
            textStyle = TextStyle().setFontSize(60F).setColor(this@YgoCard.color)
                .setFontFamilies(arrayOf("YGO-DIY-GB", "YGO-DIY-2-BIG5", "YGODIY-JP", "YGODIY-MatrixBoldSmallCaps"))
        }

        return ParagraphBuilder(style, fonts).addText(name).build()
    }

    private fun type(): Paragraph {
        val style = ParagraphStyle().apply {
            maxLinesCount = 1
            alignment = Alignment.RIGHT
            textStyle = TextStyle().setFontSize(48F).setColor(Color.BLACK)
                .setFontFamilies(arrayOf("YGO-DIY-GB", "YGO-DIY-2-BIG5", "YGODIY-JP", "YGODIY-MatrixBoldSmallCaps"))
        }
        return ParagraphBuilder(style, fonts).addText(type).build()
    }

    private fun race(): Paragraph {
        val style = ParagraphStyle().apply {
            maxLinesCount = 1
            textStyle = TextStyle().setFontSize(26F).setColor(Color.BLACK)
                .setFontFamilies(arrayOf("YGO-DIY-GB", "YGO-DIY-2-BIG5", "YGODIY-JP", "YGODIY-MatrixBoldSmallCaps"))
        }
        val text = race.joinToString(separator = "/", prefix = "【", postfix = "】")

        return ParagraphBuilder(style, fonts).addText(text).build()
    }

    private fun description(): Paragraph {
        val style = ParagraphStyle().apply {
            maxLinesCount = 9
            textStyle = TextStyle().setFontSize(24F).setColor(Color.BLACK)
                .setFontFamilies(arrayOf("YGO-DIY-GB", "YGO-DIY-2-BIG5", "YGODIY-JP", "YGODIY-MatrixBoldSmallCaps"))
        }

        return ParagraphBuilder(style, fonts).addText(description).build()
    }

    private fun value(text: String): Paragraph {
        val style = ParagraphStyle().apply {
            maxLinesCount = 1
            alignment = Alignment.RIGHT
            textStyle = TextStyle().setFontSize(36F).setColor(Color.BLACK)
                .setFontFamilies(arrayOf("YGODIY-MatrixBoldSmallCaps"))
        }

        return ParagraphBuilder(style, fonts).addText(text).build()
    }

    private fun copyright(): Paragraph {
        val style = ParagraphStyle().apply {
            maxLinesCount = 1
            alignment = Alignment.RIGHT
            textStyle = TextStyle().setFontSize(18F).setColor(Color.BLACK)
                .setFontFamilies(arrayOf("FOT-Rodin Pro"))
        }
        val text = this.copyright ?: when (locale) {
            Locale.JAPAN -> "ⓒスタジオ·ダイス /集英社·テレビ東京·KONAMI"
            Locale.ENGLISH -> "ⓒ1996 KAZUKI TAKAHASHI"
            Locale.CHINA -> "ⓒ2020 Studio Dice/SHUEISHA, TV TOKYO,KONAMI"
            else -> "ⓒ高橋和希 スタジオ・ダイス/集英社 "
        }

        return ParagraphBuilder(style, fonts).addText(text).build()
    }

    /**
     * @param project [https://github.com/ymssx/ygo-card/archive/refs/heads/master.zip]
     */
    public fun render(project: File = File(System.getProperty(SOURCE_KEY, "."))): Surface {
        project.resolve("source/mold/font").walk().forEach { file ->
            try {
                FontUtils.loadTypeface(file.path)
            } catch (_: Throwable) {
                //
            }
        }
        val surface = Surface.makeRasterN32Premul(813, 1185)
        val canvas = surface.canvas

        // draw frame
        val frame = frame(folder = project)
        canvas.drawImage(Image.makeFromEncoded(frame.readBytes()), 0F, 0F)

        // draw name
        name().layout(615F).paint(canvas, 65F, 63F)

        // draw attribute
        val attribute = project.resolve("source/mold/attribute/${locale.language}/${attribute}.png")
        canvas.drawImageRect(Image.makeFromEncoded(attribute.readBytes()), Rect.makeXYWH(680F, 57F, 75F, 75F))

        // draw Type
        type().layout(616F).paint(canvas, 132F, 148F)

        // draw Star
        if (this is Monster) {
            val star = Image.makeFromEncoded(project.resolve("source/mold/star/level.png").readBytes())
            repeat(level) { index ->
                canvas.drawImageRect(star, Rect.makeXYWH(686F - index * 55, 145F, 50F, 50F))
            }
        }

        // draw face
        canvas.drawImageRect(face, Rect.makeXYWH(100F, 219F, 614F, 616F))

        // draw description

        if (this is Monster) {
            race().layout(610F).paint(canvas, 53F, 896F)
            description().layout(681F).paint(canvas, 66F, 923F)
        } else {
            description().layout(681F).paint(canvas, 66F, 896F)
        }

        // draw ATK/DEF
        if (this is Monster) {
            canvas.drawLine(64F, 1079F, 64F + 683, 1079F, Paint().apply {
                color = Color.BLACK
                strokeWidth = 2F
            })
            value("ATK/").layout(100F).paint(canvas, 413F, 1080F)
            value(attack).layout(100F).paint(canvas, 485F, 1080F)
            value("DEF/").layout(100F).paint(canvas, 578F, 1080F)
            value(defend).layout(100F).paint(canvas, 650F, 1080F)
        }

        // draw copyright
        copyright().layout(647F).paint(canvas, 83F, 1122F)

        return surface
    }

    public companion object {
        @PublishedApi
        internal const val SOURCE_KEY: String = "xyz.cssxsh.skia.ygo"
    }
}
