package xyz.cssxsh.mirai.meme

import kotlinx.coroutines.*
import net.mamoe.mirai.console.*
import net.mamoe.mirai.console.extension.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.console.plugin.*
import net.mamoe.mirai.console.util.*
import net.mamoe.mirai.event.*

public object MemeHelperPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "xyz.cssxsh.mirai.plugin.meme-helper",
        name = "meme-helper",
        version = "1.2.0",
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
        // XXX: mirai console version check
        check(SemVersion.parseRangeRequirement(">= 2.12.0-RC").test(MiraiConsole.version)) {
            "$name $version 需要 Mirai-Console 版本 >= 2.12.0，目前版本是 ${MiraiConsole.version}"
        }
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