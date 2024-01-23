package xyz.cssxsh.mirai.meme

import com.itextpdf.io.font.woff2.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.jetbrains.skia.*
import org.jetbrains.skia.paragraph.*
import xyz.cssxsh.skia.FontUtils
import java.io.Closeable
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.zip.ZipFile
import kotlin.math.*

/**
 * [The Original Ayaka](https://github.com/TheOriginalAyaka/sekai-stickers)
*/
public class ProjectSekaiStickers private constructor(private val source: ZipFile) : Closeable {
    public constructor(path: String) : this(source = ZipFile(path))

    private fun ZipFile.getInputStream(name: String): InputStream {
        val entry = getEntry(name) ?: throw FileNotFoundException(name)
        return getInputStream(entry)
    }

    private val characters: List<Character> by lazy {
        source.getInputStream("sekai-stickers-main/src/characters.json").use { input ->
            @OptIn(ExperimentalSerializationApi::class)
            Json.decodeFromStream<List<Character>>(input)
        }
    }

    private val fonts: FontCollection by lazy {
        source.getInputStream("sekai-stickers-main/src/fonts/YurukaStd.woff2").use { input ->
            FontUtils.loadTypeface(Woff2Converter.convert(input.readAllBytes()))
        }
        source.getInputStream("sekai-stickers-main/src/fonts/ShangShouFangTangTi.woff2").use { input ->
            FontUtils.loadTypeface(Woff2Converter.convert(input.readAllBytes()))
        }
        FontCollection()
            .setDynamicFontManager(FontUtils.provider)
            .setDefaultFontManager(FontMgr.default)
    }

    internal fun image(character: Character): Image {
        val bytes = source.getInputStream("sekai-stickers-main/public/img/${character.image}")
            .use { it.readAllBytes() }
        return Image.makeFromEncoded(bytes)
    }

    public fun create(name: String, block: Content.() -> Unit): Image {
        val character = characters.find { it.name == name } ?: throw NoSuchElementException(name)

        val surface = Surface.makeRasterN32Premul(296, 256)
        val canvas = surface.canvas
        val image = image(character = character)
        val content = character.default.apply(block)

        val hRatio = surface.width.toFloat() / image.width
        val vRatio = surface.height.toFloat() / image.height
        val ratio = minOf(hRatio, vRatio)
        canvas.drawImageRect(
            image = image,
            dst = Rect.makeXYWH(
                l = (surface.width - image.width * ratio) / 2,
                t = (surface.height - image.height * ratio) / 2,
                w = image.width * ratio,
                h = image.height * ratio
            )
        )

        val rad = content.rotate / 10
        val sin = sin(rad)
        val cos = cos(rad)
        val m = Matrix33(
            cos, -sin, content.x - content.x * cos + content.y * sin,
            sin, cos, content.y - content.y * cos - content.y * sin,
            0f, 0f, 1f
        )
        canvas.concat(m)

        val style = ParagraphStyle().apply {
            maxLinesCount = 3
            alignment = Alignment.CENTER
        }

        ParagraphBuilder(style, fonts)
            .pushStyle(TextStyle()
                .setFontSize(content.size)
                .setForeground(Paint().apply {
                    strokeCap = PaintStrokeCap.ROUND
                    strokeJoin = PaintStrokeJoin.ROUND
                    strokeWidth = 10F
                    color = Color.WHITE
                    mode = PaintMode.STROKE
                })
                .setFontFamilies(arrayOf("FOT-Yuruka Std UB", "YurukaStd", "SSFangTangTi")))
            .addText(content.text)
            .build()
            .layout(surface.width.toFloat())
            .paint(canvas, -6F, 6F)


        ParagraphBuilder(style, fonts)
            .pushStyle(TextStyle()
                .setFontSize(content.size)
                .setForeground(Paint().apply {
                    color = character.color.replace("#", "FF").toLong(16).toInt()
                    mode = PaintMode.FILL
                })
                .setFontFamilies(arrayOf("FOT-Yuruka Std UB", "YurukaStd", "SSFangTangTi")))
            .addText(content.text)
            .build()
            .layout(surface.width.toFloat())
            .paint(canvas, -6F, 6F)

        return surface.makeImageSnapshot()
    }

    override fun close(): Unit = source.close()

    @Serializable
    internal data class Character(
        @SerialName("character")
        val category: String,
        @SerialName("color")
        val color: String,
        @SerialName("defaultText")
        val default: Content,
        @SerialName("id")
        val id: String,
        @SerialName("img")
        val image: String,
        @SerialName("name")
        val name: String
    )

    @Serializable
    public data class Content(
        @SerialName("r")
        var rotate: Float = 0F,
        @SerialName("s")
        var size: Float = 0F,
        @SerialName("text")
        var text: String = "",
        @SerialName("x")
        var x: Float = 0F,
        @SerialName("y")
        var y: Float = 0F
    )
}