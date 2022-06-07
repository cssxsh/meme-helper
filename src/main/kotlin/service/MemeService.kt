package xyz.cssxsh.mirai.meme.service

import kotlinx.coroutines.*
import net.mamoe.mirai.console.permission.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.*
import kotlin.coroutines.*
import kotlin.jvm.*

/**
 * 表情包服务接口
 * @see MemeHandler
 */
public interface MemeService {

    /**
     * 服务名称
     */
    public val name: String

    /**
     * 服务ID，将用来构造权限ID和设置缓存文件夹
     */
    public val id: String

    /**
     * 简介
     */
    public val description: String

    /**
     * 已加载
     */
    public val loaded: Boolean

    /**
     * 配置，将会映射到文件
     */
    public val properties: Properties

    /**
     * 权限, 应该为 [load] 的 permission 参数
     */
    public val permission: Permission

    /**
     *
     */
    public val regex: Regex

    /**
     * 表情包生成
     */
    public suspend fun MessageEvent.replier(match: MatchResult): Message?

    /**
     * 加载接口，在 [xyz.cssxsh.mirai.meme.MemeHelperPlugin.onEnable] 时触发
     * @param folder 缓存文件夹
     * @see xyz.cssxsh.skia.FontUtils
     */
    @Throws(IOException::class)
    public fun load(folder: File, permission: Permission)

    /**
     * 启动接口，在 [xyz.cssxsh.mirai.meme.MemeHelper.online] 时触发
     */
    public fun enable()

    /**
     * 关闭接口，在 [xyz.cssxsh.mirai.meme.MemeHelperPlugin.onDisable] 时触发
     */
    public fun disable()

    public companion object Loader : Sequence<MemeService>, CoroutineScope {
        override var coroutineContext: CoroutineContext = EmptyCoroutineContext
            internal set

        internal val instances: MutableSet<MemeService> = HashSet()

        public operator fun get(id: String): MemeService? = instances.find { it.id == id }

        override fun iterator(): Iterator<MemeService> = instances.iterator()
    }
}