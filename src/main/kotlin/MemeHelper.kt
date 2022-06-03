package xyz.cssxsh.mirai.meme

import net.mamoe.mirai.contact.*
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
                logger.info { result.value }
                val (porn, hub) = result.destructured

                pornhub(porn, hub).makeSnapshotResource()
                    .use { resource -> subject.uploadImage(resource = resource) }
            }
            regex.petpet findingReply { result ->
                logger.info { result.value }
                val id = result.groups[1]?.value?.toLongOrNull()
                    ?: message.findIsInstance<At>()?.target
                    ?: sender.id
                val avatar = avatar(id = id)

                SkiaExternalResource(origin = petpet(avatar), formatName = "gif")
                    .use { resource -> subject.uploadImage(resource = resource) }
            }
            regex.dear findingReply { result ->
                logger.info { result.value }
                val id = result.groups[1]?.value?.toLongOrNull()
                    ?: message.findIsInstance<At>()?.target
                    ?: sender.id
                val avatar = avatar(id = id)
                val dear = dear(avatar)

                dear.uploadAsImage(subject)
            }
            regex.choyen findingReply { result ->
                logger.info { result.value }
                val (top, bottom) = result.destructured

                choyen(top, bottom).makeSnapshotResource()
                    .use { resource -> subject.uploadImage(resource = resource) }
            }
            regex.zzkia findingReply { result ->
                logger.info { result.value }
                val (_, text) = result.destructured

                zzkia(text).makeSnapshotResource()
                    .use { resource -> subject.uploadImage(resource = resource) }
            }

            regex.random findingReply { random() }
            regex.md5 findingReply { md5(id = it.value) }

            """^#spell\s*(\d+)?""".toRegex() findingReply { result ->
                logger.info { result.value }
                val id = result.groups[1]?.value?.toLongOrNull()
                    ?: message.findIsInstance<At>()?.target
                    ?: sender.id
                val face = avatar(id = id, size = 640)
                val lines = message.contentToString().lines()
                val member = (subject as? Group)?.get(id)
                val name = lines.getOrNull(1)?.takeIf { it.isNotBlank() }
                    ?: member?.remarkOrNameCardOrNick
                    ?: senderName
                val description = lines.filterIndexed { index, _ -> index > 1 }.joinToString("\n")
                    .ifBlank { (member ?: sender).queryProfile().sign }

                YgoCard.Spell(name = name, description = description, face = face)
                    .render()
                    .makeSnapshotResource()
                    .use { resource -> subject.uploadImage(resource = resource) }
            }
            """^#trap\s*(\d+)?""".toRegex() findingReply { result ->
                logger.info { result.value }
                val id = result.groups[1]?.value?.toLongOrNull()
                    ?: message.findIsInstance<At>()?.target
                    ?: sender.id
                val face = avatar(id = id, size = 640)
                val lines = message.contentToString().lines()
                val member = (subject as? Group)?.get(id)
                val name = lines.getOrNull(1)?.takeIf { it.isNotBlank() }
                    ?: member?.remarkOrNameCardOrNick
                    ?: senderName
                val description = lines.filterIndexed { index, _ -> index > 1 }.joinToString("\n")
                    .ifBlank { (member ?: sender).queryProfile().sign }

                YgoCard.Trap(name = name, description = description, face = face)
                    .render()
                    .makeSnapshotResource()
                    .use { resource -> subject.uploadImage(resource = resource) }
            }
            """^#monster\s*(\d+)?""".toRegex() findingReply { result ->
                logger.info { result.value }
                val id = result.groups[1]?.value?.toLongOrNull()
                    ?: message.findIsInstance<At>()?.target
                    ?: sender.id
                val face = avatar(id = id, size = 640)
                val lines = message.contentToString().lines()
                val member = (subject as? Group)?.get(id)
                val name = lines.getOrNull(1)?.takeIf { it.isNotBlank() }
                    ?: member?.remarkOrNameCardOrNick
                    ?: senderName
                val profile = (member ?: sender).queryProfile()
                val race = lines.getOrNull(2)?.takeIf { it.isNotBlank() }?.split(',', ' ', '/')
                    ?: listOfNotNull(member?.specialTitle, member?.permission?.name, profile.sex.name)
                val description = lines.filterIndexed { index, _ -> index > 2 }.joinToString("\n")
                    .ifBlank { profile.sign }
                val level = (profile.qLevel / 16) + 1
                val attribute = YgoCard.Attribute.monster().random()
                YgoCard.Monster(name = name, description = description, face = face, level = level, race = race, attribute = attribute)
                    .render()
                    .makeSnapshotResource()
                    .use { resource -> subject.uploadImage(resource = resource) }
            }
        }

        return ListeningStatus.STOPPED
    }
}