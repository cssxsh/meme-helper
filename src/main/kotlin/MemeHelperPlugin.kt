package xyz.cssxsh.mirai.meme

import kotlinx.coroutines.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.event.*
import xyz.cssxsh.mirai.meme.data.*

public object MemeHelperPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "xyz.cssxsh.mirai.plugin.meme-helper",
        name = "meme-helper",
        version = "1.0.0-dev",
    ) {
        author("cssxsh")
        dependsOn("xyz.cssxsh.mirai.plugin.mirai-skia-plugin", ">= 1.1.0", false)
        dependsOn("xyz.cssxsh.mirai.plugin.mirai-hibernate-plugin", ">= 2.2.0", true)
    }
) {

    override fun onEnable() {
        MemeRegexConfig.reload()
        MemeHelper.registerTo(globalEventChannel())
        avatarFolder = resolveDataFile("avatar").apply { mkdirs() }

        launch {
            loadMaterial(folder = resolveDataFile("material"))
        }
    }

    override fun onDisable() {
        MemeHelper.cancelAll()
    }
}