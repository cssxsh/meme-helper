package xyz.cssxsh.mirai.meme

import net.mamoe.mirai.console.extension.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.meme.service.*

public object MemeHelperPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "xyz.cssxsh.mirai.plugin.meme-helper",
        name = "meme-helper",
        version = "1.0.3",
    ) {
        author("cssxsh")
        dependsOn("xyz.cssxsh.mirai.plugin.mirai-skia-plugin", ">= 1.1.0", false)
        dependsOn("xyz.cssxsh.mirai.plugin.mirai-hibernate-plugin", ">= 2.2.0", true)
        dependsOn("xyz.cssxsh.mirai.plugin.weibo-helper", true)
        dependsOn("xyz.cssxsh.mirai.plugin.bilibili-helper", true)
    }
) {

    override fun PluginComponentStorage.onLoad() {
        runAfterStartup {
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
    }

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