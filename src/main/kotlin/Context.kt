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
import org.jetbrains.skia.Image as SkiaImage
import xyz.cssxsh.mirai.hibernate.entry.*
import xyz.cssxsh.mirai.hibernate.*
import xyz.cssxsh.mirai.meme.data.*
import java.io.File

public fun random(): MessageContent = FaceRecord.random().toMessageContent()

public fun md5(id: String): MessageContent? = MiraiHibernateRecorder.face(md5 = id)?.toMessageContent()

internal lateinit var avatarFolder: File

internal val http = HttpClient(OkHttp) {
    CurlUserAgent()
    install(HttpTimeout) {
        connectTimeoutMillis = 30_000
        socketTimeoutMillis = 30_000
    }
}

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

