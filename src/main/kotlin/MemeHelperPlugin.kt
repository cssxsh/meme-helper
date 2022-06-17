package xyz.cssxsh.mirai.meme

import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.event.*

public object MemeHelperPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "xyz.cssxsh.mirai.plugin.meme-helper",
        name = "meme-helper",
        version = "1.0.2",
    ) {
        author("cssxsh")
        dependsOn("xyz.cssxsh.mirai.plugin.mirai-skia-plugin", ">= 1.1.0", false)
        dependsOn("xyz.cssxsh.mirai.plugin.mirai-hibernate-plugin", ">= 2.2.0", true)
        dependsOn("xyz.cssxsh.mirai.plugin.weibo-helper", true)
        dependsOn("xyz.cssxsh.mirai.plugin.bilibili-helper", true)
    }
) {

    override fun onEnable() {
        MemeHelper.registerTo(globalEventChannel())
        avatarFolder = resolveDataFile("avatar").apply { mkdirs() }
        imageFolder = resolveDataFile("image").apply { mkdirs() }

        loadMemeService()
    }

    override fun onDisable() {
        MemeHelper.cancelAll()
        saveMemeService()
    }
}