package xyz.cssxsh.mirai.meme.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.mirai.meme.*

public object FaceCommand : SimpleCommand(
    owner = MemeHelperPlugin,
    "face",
    description = "从消息记录中获取表情"
) {
    @Handler
    public suspend fun CommandSenderOnMessage<*>.handle(tag: String = "") {
        val face = when {
            tag.length == 16 -> md5(id = tag) ?: "查找表情失败".toPlainText()
            tag.isNotEmpty() -> TODO()
            else -> random()
        }

        sendMessage(face)
    }
}