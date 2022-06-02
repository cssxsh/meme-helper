package xyz.cssxsh.mirai.meme.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.utils.ExternalResource.Companion.sendAsImageTo
import xyz.cssxsh.mirai.meme.*
import xyz.cssxsh.mirai.skia.*
import xyz.cssxsh.skia.*

public object MemeCommand : CompositeCommand(
    owner = MemeHelperPlugin,
    "meme",
    description = "从消息记录中获取表情"
) {
    @SubCommand("pornhub")
    public suspend fun CommandSenderOnMessage<*>.pornhub0(porn: String, hub: String) {
        pornhub(porn, hub).makeSnapshotResource().use { it.sendAsImageTo(fromEvent.subject) }
    }

    @SubCommand("petpet")
    public suspend fun CommandSenderOnMessage<*>.pet0(target: Contact = fromEvent.sender) {
        val avatar = avatar(id = target.id)

        SkiaExternalResource(origin = petpet(avatar), formatName = "gif").use { it.sendAsImageTo(fromEvent.subject) }
    }

    @SubCommand("dear")
    public suspend fun CommandSenderOnMessage<*>.dear0(target: Contact = fromEvent.sender) {
        val avatar = avatar(id = target.id)

        dear(face = avatar).sendAsImageTo(fromEvent.subject)
    }

    @SubCommand("choyen")
    public suspend fun CommandSenderOnMessage<*>.choyen0(top: String, bottom: String) {
        choyen(top, bottom).makeSnapshotResource().use { it.sendAsImageTo(fromEvent.subject) }
    }

    @SubCommand("pinyin")
    public suspend fun CommandSenderOnMessage<*>.pinyin0() {
        val text = fromEvent.message.contentToString()
            .removePrefix("pinyin").removePrefix(" ")

        zzkia(text).makeSnapshotResource().use { it.sendAsImageTo(fromEvent.subject) }
    }
}