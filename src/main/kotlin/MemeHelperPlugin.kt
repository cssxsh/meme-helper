package xyz.cssxsh.mirai.meme

import kotlinx.coroutines.*
import net.mamoe.mirai.console.extension.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.event.*

@PublishedApi
internal object MemeHelperPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "xyz.cssxsh.mirai.plugin.meme-helper",
        name = "meme-helper",
        version = "1.4.0"
    ) {
        author("cssxsh")
        dependsOn("xyz.cssxsh.mirai.plugin.mirai-skia-plugin", ">= 1.1.0", false)
        dependsOn("xyz.cssxsh.mirai.plugin.mirai-hibernate-plugin", ">= 2.4.0", true)
        dependsOn("xyz.cssxsh.mirai.plugin.weibo-helper", true)
        dependsOn("xyz.cssxsh.mirai.plugin.bilibili-helper", true)
    }
) {

    override fun PluginComponentStorage.onLoad() {
        loadMemeService()
    }

    override fun onEnable() {
        avatarFolder = resolveDataFile("avatar").apply { mkdirs() }
        imageFolder = resolveDataFile("image").apply { mkdirs() }
        enableMemeService()
        MemeHelper.registerTo(globalEventChannel())
    }

    override fun onDisable() {
        MemeHelper.cancel()
        disableMemeService()
    }
}