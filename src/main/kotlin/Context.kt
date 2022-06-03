package xyz.cssxsh.mirai.meme

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import org.jetbrains.skia.Image as SkiaImage
import xyz.cssxsh.mirai.hibernate.entry.*
import xyz.cssxsh.mirai.hibernate.*
import xyz.cssxsh.mirai.meme.data.*
import xyz.cssxsh.skia.*
import java.io.File
import java.util.zip.*

internal val logger by lazy {
    val open = System.getProperty("xyz.cssxsh.mirai.meme.logger", "true").toBoolean()
    if (open) MemeHelperPlugin.logger else SilentLogger
}

/**
 * 随机一份消息记录器里的表情
 * @see MiraiHibernateRecorder
 */
public fun random(): MessageContent = FaceRecord.random().toMessageContent()

/**
 * 获取消息记录器里的表情
 * @param id 表情的ID，文件的MD5
 * @see MiraiHibernateRecorder
 */
public fun md5(id: String): MessageContent? = MiraiHibernateRecorder.face(md5 = id)?.toMessageContent()

internal lateinit var avatarFolder: File

internal val http = HttpClient(OkHttp) {
    CurlUserAgent()
    install(HttpTimeout) {
        connectTimeoutMillis = 30_000
        socketTimeoutMillis = 30_000
    }
}

/**
 * 下载用户头像
 * @param id 用户的ID
 * @param size 图片尺寸
 */
public suspend fun avatar(id: Long, size: Int = 140): SkiaImage {
    val cache = http.get<HttpStatement>("https://q.qlogo.cn/g?b=qq&nk=${id}&s=${size}").execute { response ->
        val file = avatarFolder.resolve("${id}.${size}.${response.contentType()?.contentSubtype}")
        if (file.exists().not() || file.lastModified() < (response.lastModified()?.time ?: 0)) {
            file.outputStream().use { output ->
                val channel: ByteReadChannel = response.receive()

                while (!channel.isClosedForRead) channel.copyTo(output)
            }
        } else {
            response.call.cancel("文件 ${file.name} 已存在，跳过下载")
        }
        file
    }

    return SkiaImage.makeFromEncoded(bytes = cache.readBytes())
}

internal val regex: MemeRegex get() = MemeRegexConfig

internal suspend fun download(urlString: String, folder: File): File = supervisorScope {
    http.get<HttpStatement>(urlString).execute { response ->
        val relative = response.headers[HttpHeaders.ContentDisposition]
            ?.let { ContentDisposition.parse(it).parameter(ContentDisposition.Parameters.FileName) }
            ?: response.request.url.encodedPath.substringAfterLast('/').decodeURLPart()

        val file = folder.resolve(relative)

        if (file.exists()) {
            logger.info { "文件 ${file.name} 已存在，跳过下载" }
            response.call.cancel("文件 ${file.name} 已存在，跳过下载")
        } else {
            logger.info { "文件 ${file.name} 开始下载" }
            file.outputStream().use { output ->
                val channel: ByteReadChannel = response.receive()

                while (!channel.isClosedForRead) channel.copyTo(output)
            }
        }

        file
    }
}

/**
 * 下载表情模板
 */
internal suspend fun loadMaterial(folder: File): Unit = supervisorScope {
    folder.mkdirs()
    val sprite = folder.resolve("sprite.png")
    if (sprite.exists().not()) {
        download(urlString = "https://benisland.neocities.org/petpet/img/sprite.png", folder)
            .renameTo(sprite)
    }
    System.setProperty(PET_PET_SPRITE, sprite.absolutePath)

    val dear = folder.resolve("dear.gif")
    if (dear.exists().not()) {
        download(urlString = "https://tva3.sinaimg.cn/large/003MWcpMly8gv4s019bzsg606o06o40902.gif", folder)
            .renameTo(dear)
    }
    System.setProperty(DEAR_ORIGIN, dear.absolutePath)

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

    val ygo = folder.resolve("ygo-card-master")
    if (ygo.exists().not()) {
        val file = try {
            download(urlString = "https://github.com/ymssx/ygo-card/archive/refs/heads/master.zip", folder)
        } catch (_: Exception) {
            download(urlString = "https://download.fastgit.org/ymssx/ygo-card/archive/master.zip", folder)
        }
        runInterruptible(Dispatchers.IO) {
            ZipFile(file).use { zip ->
                for (entry in zip.entries()) {
                    val item = folder.resolve(entry.name)
                    if (entry.isDirectory) {
                        item.mkdirs()
                        continue
                    }
                    item.outputStream().use { zip.getInputStream(entry).transferTo(it) }
                }
            }
        }
    }
    System.setProperty(YgoCard.SOURCE_KEY, ygo.path)
}

