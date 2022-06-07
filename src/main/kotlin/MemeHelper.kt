package xyz.cssxsh.mirai.meme

import net.mamoe.mirai.console.command.CommandSender.Companion.toCommandSender
import net.mamoe.mirai.console.permission.PermissionService.Companion.hasPermission
import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.meme.service.*
import kotlin.coroutines.*
import kotlin.coroutines.cancellation.*

public object MemeHelper : SimpleListenerHost() {

    /**
     * 在 第一个机器人上线之后，将启动 MemeService
     */
    @EventHandler(concurrency = ConcurrencyKind.CONCURRENT)
    public fun BotOnlineEvent.online(): ListeningStatus {
        bot.id
        for (service in MemeService) {
            if (!service.loaded) continue
            service.enable()
            logger.info { "enable: ${service.name} - ${service.permission}" }
        }

        return ListeningStatus.STOPPED
    }

    @EventHandler
    public suspend fun MessageEvent.handle() {
        for (service in MemeService) {
            if (!service.loaded) continue
            if (!toCommandSender().hasPermission(service.permission)) continue

            val match = service.regex.find(input = message.content) ?: continue

            val message = with(service) { replier(match) } ?: continue

            subject.sendMessage(message = message)
        }
    }

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        when (exception) {
            is ExceptionInEventHandlerException -> logger.warning({ "MemeHelper Handle Exception" }, exception.cause)
            is CancellationException -> Unit
            else -> logger.warning({ "MemeHelper Exception" }, exception)
        }
    }
}