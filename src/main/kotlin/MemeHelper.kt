package xyz.cssxsh.mirai.meme

import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.skia.*
import xyz.cssxsh.skia.*

public object MemeHelper : SimpleListenerHost() {

    @EventHandler(concurrency = ConcurrencyKind.CONCURRENT)
    public fun BotOnlineEvent.online(): ListeningStatus {
        bot.id
        globalEventChannel().subscribeMessages {
            regex.pornhub findingReply { result ->
                MiraiSkiaPlugin.logger.info { result.value }
                val (porn, hub) = result.destructured

                pornhub(porn, hub).makeSnapshotResource()
                    .use { resource -> subject.uploadImage(resource = resource) }
            }
            regex.petpet findingReply { result ->
                MiraiSkiaPlugin.logger.info { result.value }
                val id = result.groups[1]?.value?.toLongOrNull()
                    ?: message.findIsInstance<At>()?.target
                    ?: sender.id
                val avatar = avatar(id = id)

                SkiaExternalResource(origin = petpet(avatar), formatName = "gif")
                    .use { resource -> subject.uploadImage(resource = resource) }
            }
            regex.dear findingReply { result ->
                MiraiSkiaPlugin.logger.info { result.value }
                val id = result.groups[1]?.value?.toLongOrNull()
                    ?: message.findIsInstance<At>()?.target
                    ?: sender.id
                val avatar = avatar(id = id)
                val dear = dear(avatar)

                dear.uploadAsImage(subject)
            }
            regex.choyen findingReply { result ->
                MiraiSkiaPlugin.logger.info { result.value }
                val (top, bottom) = result.destructured

                choyen(top, bottom).makeSnapshotResource()
                    .use { resource -> subject.uploadImage(resource = resource) }
            }
            regex.zzkia findingReply { result ->
                MiraiSkiaPlugin.logger.info { result.value }
                val (_, text) = result.destructured

                zzkia(text).makeSnapshotResource()
                    .use { resource -> subject.uploadImage(resource = resource) }
            }

            regex.random findingReply { random() }
            regex.md5 findingReply { md5(id = it.value) }
        }

        return ListeningStatus.STOPPED
    }
}