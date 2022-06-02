package xyz.cssxsh.mirai.meme

import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.event.*
import xyz.cssxsh.mirai.meme.command.*
import xyz.cssxsh.mirai.meme.data.*

public object MemeHelperPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "xyz.cssxsh.mirai.plugin.meme-helper",
        name = "meme-helper",
        version = "1.0.0-dev",
    ) {
        author("cssxsh")
        dependsOn("xyz.cssxsh.mirai.plugin.mirai-skia-plugin", ">= 1.0.4", false)
        dependsOn("xyz.cssxsh.mirai.plugin.mirai-hibernate-plugin", ">= 2.2.0", true)
    }
) {

    override fun onEnable() {
        MemeRegexConfig.reload()
        FaceCommand.register()
        MemeCommand.register()
        MemeHelper.registerTo(globalEventChannel())

        avatarFolder = resolveDataFile("avatar").apply { mkdirs() }
    }

    override fun onDisable() {
        FaceCommand.unregister()
        MemeCommand.unregister()
        MemeHelper.cancelAll()
    }
}