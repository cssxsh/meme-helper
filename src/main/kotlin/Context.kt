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
import net.mamoe.mirai.console.permission.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.utils.*
import org.jetbrains.skia.Image as SkiaImage
import xyz.cssxsh.mirai.meme.service.*
import java.io.File
import java.time.*
import java.util.*

internal val logger by lazy {
    try {
        MemeHelperPlugin.logger
    } catch (_: Throwable) {
        MiraiLogger.Factory.create(MemeHelper::class)
    }
}

internal lateinit var avatarFolder: File

internal lateinit var imageFolder: File

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

public suspend fun cache(image: Image): SkiaImage {
    val md5 = image.md5.toUHexString(separator = "")
    val cache = imageFolder.listFiles { file -> file.name.startsWith(prefix = md5) }?.firstOrNull()
        ?: http.get<HttpStatement>(image.queryUrl()).execute { response ->
        val file = imageFolder.resolve("${md5}.${response.contentType()?.contentSubtype}")
        file.outputStream().use { output ->
            val channel: ByteReadChannel = response.receive()

            while (!channel.isClosedForRead) channel.copyTo(output)
        }
        file
    }

    return SkiaImage.makeFromEncoded(bytes = cache.readBytes())
}

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

internal fun JvmPlugin.loadMemeService() {
    MemeService.coroutineContext = childScopeContext("MemeServiceLoader", Dispatchers.IO)
    val services = sequence<MemeService> {
        @OptIn(MiraiInternalApi::class)
        for (classLoader in MemeHelperPlugin.loader.classLoaders) {
            val iterator = ServiceLoader.load(MemeService::class.java, classLoader).iterator()
            while (iterator.hasNext()) {
                try {
                    val service = iterator.next()
                    if (MemeService[service.id] != null) {
                        logger.verbose { "${service.id} 加载重复" }
                        continue
                    }

                    yield(service)
                } catch (cause: Throwable) {
                    logger.info({ "MemeService 加载失败" }, cause)
                    continue
                }
            }
        }
    }
    for (service in services) {
        try {
            val properties = dataFolder.resolve("${service.id}.properties")
            if (properties.exists()) {
                properties.inputStream().use { input ->
                    service.properties.load(input)
                }
            } else {
                properties.outputStream().use { output ->
                    service.properties.store(output, "${OffsetDateTime.now()}")
                }
            }

            val permission = PermissionService.INSTANCE.register(
                id = permissionId(service.id),
                description = service.description,
                parent = parentPermission
            )

            val folder = dataFolder.resolve(service.id).apply { mkdirs() }

            service.load(folder = folder, permission = permission)
        } catch (cause: Throwable) {
            logger.info({ "${service.name} 加载失败" }, cause)
            continue
        }

        MemeService.instances.add(service)
        logger.info { "${service.name} 加载成功" }
    }
}

internal fun JvmPlugin.enableMemeService() {
    for (service in MemeService) {
        if (!service.loaded) continue
        try {
            service.enable()
            logger.info { "enable success, ${service.name} - ${service.permission}" }
        } catch (cause: Throwable) {
            logger.warning({ "enable failure: ${service.name} - ${cause.message}" }, cause)
        }
    }
}

internal fun JvmPlugin.disableMemeService() {
    for (service in MemeService) {
        if (!service.loaded) continue
        service.disable()
        dataFolder.resolve("${service.id}.properties").outputStream().use { output ->
            service.properties.store(output, "${OffsetDateTime.now()}")
        }
    }
}

