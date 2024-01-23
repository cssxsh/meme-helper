package xyz.cssxsh.mirai.meme

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.jetbrains.skia.*
import org.jetbrains.skia.paragraph.*
import xyz.cssxsh.skia.FontUtils
import java.io.Closeable
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.zip.ZipFile

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
        FontCollection()
            .setDynamicFontManager(FontUtils.provider)
            .setDefaultFontManager(FontMgr.default)
    }

    internal fun image(character: Character): Image {
        val bytes = source.getInputStream("sekai-stickers-main/public/img/${character.image}")
            .use { it.readAllBytes() }
        return Image.makeFromEncoded(bytes)
    }

    public fun create(name: String): Image {
        val character = characters.find { it.name == name } ?: throw NoSuchElementException(name)

        val surface = Surface.makeRasterN32Premul(296, 256)
        val canvas = surface.canvas
        val image = image(character = character)
        val content = character.default

        canvas.drawImage(image, 0F, 0F)
        
        canvas.translate(-10F,  content.y + 20)
        canvas.rotate(content.rotate * 10)

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
                .setFontFamilies(arrayOf("YurukaStd", "SSFangTangTi")))
            .addText(content.text)
            .build()
            .layout(surface.width.toFloat())
            .paint(canvas, 0F, 0F)


        ParagraphBuilder(style, fonts)
            .pushStyle(TextStyle()
                .setFontSize(content.size)
                .setForeground(Paint().apply {
                    color = character.color.replace("#", "FF").toLong(16).toInt()
                    mode = PaintMode.FILL
                })
                .setFontFamilies(arrayOf("YurukaStd", "SSFangTangTi")))
            .addText(content.text)
            .build()
            .layout(surface.width.toFloat())
            .paint(canvas, 0F, 0F)

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
    internal data class Content(
        @SerialName("r")
        val rotate: Float = 0F,
        @SerialName("s")
        val size: Float = 0F,
        @SerialName("text")
        val text: String = "",
        @SerialName("x")
        val x: Float = 0F,
        @SerialName("y")
        val y: Float = 0F
    )
}