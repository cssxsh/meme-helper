package xyz.cssxsh.mirai.meme.impl

import kotlinx.coroutines.*
import net.mamoe.mirai.console.permission.*
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.mirai.meme.*
import xyz.cssxsh.mirai.meme.service.*
import xyz.cssxsh.mirai.skia.makeSnapshotResource
import java.io.File
import java.util.*
import java.util.zip.*

/**
 * [游戏王制卡器](https://ymssx.github.io/ygo/#/)
 * @see YgoCard
 */
public class MemeYgo: MemeService {
    override val name: String = "Ygo"
    override val id: String = "ygo"
    override val description: String = "Ygo 卡片 生成器"
    override var loaded: Boolean = false
        private set
    override var regex: Regex = """^#(spell|trap|monster)\s*(\d+)?""".toRegex()
        private set
    override val properties: Properties = Properties().apply { put("regex", regex.pattern) }
    override var permission: Permission = Permission.getRootPermission()
        private set
    private lateinit var loadJob: Job

    override fun load(folder: File, permission: Permission) {
        this.permission = permission
        when (val re = properties["regex"]) {
            is String -> regex = re.toRegex()
            is Regex -> regex = re
            else -> {}
        }
        loadJob = MemeService.launch {
            folder.mkdirs()

            val ygo = folder.resolve("ygo-card-master")
            if (ygo.exists().not()) {
                val file = try {
                    download(urlString = "https://github.com/ymssx/ygo-card/archive/refs/heads/master.zip", folder)
                } catch (_: Exception) {
                    folder.resolve("ygo-card-master.zip").delete()
                    download(urlString = "https://download.fastgit.org/ymssx/ygo-card/archive/master.zip", folder)
                }
                runInterruptible(Dispatchers.IO) {
                    ZipFile(file).use { zip ->
                        for (entry in zip.entries()) {
                            val item = folder.resolve(entry.name)
                            if (entry.isDirectory) {
                                item.mkdirs()
                                continue
                            }
                            item.outputStream().use { zip.getInputStream(entry).transferTo(it) }
                        }
                    }
                }
                file.deleteOnExit()
            }
            ygo.resolve("source/mold/attribute/jp")
                .renameTo(ygo.resolve("source/mold/attribute/ja"))
            ygo.resolve("source/mold/attribute/cn")
                .renameTo(ygo.resolve("source/mold/attribute/zh"))
            System.setProperty(YgoCard.SOURCE_KEY, ygo.path)

            loaded = true
        }
    }

    override fun enable() {
        runBlocking {
            loadJob.join()
        }
    }

    override fun disable() {}

    override suspend fun MessageEvent.replier(match: MatchResult): Image {
        val id = match.groups[2]?.value?.toLongOrNull()
            ?: message.findIsInstance<At>()?.target
            ?: sender.id
        val image = message.findIsInstance<Image>()
        val face = when {
            image != null -> cache(image = image)
            else -> avatar(id = id, size = 640)
        }
        val lines = message.contentToString().lines().toMutableList()
        val member = (subject as? Group)?.get(id = id)
        val memberProfile = member?.queryProfile()
        val senderProfile = sender.queryProfile()
        // XXX: handle command
        lines.removeFirst()
        val name = when {
            lines.any { it.startsWith(prefix = "name=") } -> {
                val line = lines.first { it.startsWith(prefix = "name=") }
                lines.remove(element = line)
                line.removePrefix(prefix = "name=")
            }
            member != null -> member.remarkOrNameCardOrNick
            else -> sender.nameCardOrNick
        }
        val attribute = when {
            lines.any { it.startsWith(prefix = "attr=") } -> {
                val line = lines.first { it.startsWith(prefix = "attr=") }
                lines.remove(element = line)
                line.removePrefix(prefix = "attr=").let { YgoCard.Attribute.valueOf(it) }
            }
            else -> YgoCard.Attribute.monster().random()
        }
        val level = when {
            lines.any { it.startsWith(prefix = "level=") } -> {
                val line = lines.first { it.startsWith(prefix = "level=") }
                lines.remove(element = line)
                line.removePrefix(prefix = "level=").toInt()
            }
            memberProfile != null -> memberProfile.qLevel / 16 + 1
            else -> senderProfile.qLevel / 16 + 1
        }
        val race = when {
            lines.any { it.startsWith(prefix = "race=") } -> {
                val line = lines.first { it.startsWith(prefix = "race=") }
                lines.remove(element = line)
                line.removePrefix(prefix = "race=").split(',', ' ', '/')
            }
            else -> listOfNotNull(member?.specialTitle, member?.permission?.name, senderProfile.sex.name)
        }
        val attack = when {
            lines.any { it.startsWith(prefix = "atk=") } -> {
                val line = lines.first { it.startsWith(prefix = "atk=") }
                lines.remove(element = line)
                line.removePrefix(prefix = "atk=")
            }
            else -> "0"
        }
        val defend = when {
            lines.any { it.startsWith(prefix = "def=") } -> {
                val line = lines.first { it.startsWith(prefix = "def=") }
                lines.remove(element = line)
                line.removePrefix(prefix = "def=")
            }
            else -> "0"
        }
        val copyright = when {
            lines.any { it.startsWith(prefix = "copyright=") } -> {
                val line = lines.first { it.startsWith(prefix = "copyright=") }
                lines.remove(element = line)
                line.removePrefix(prefix = "copyright=")
            }
            else -> null
        }
        val description = when {
            lines.isNotEmpty() -> lines.joinToString(separator = "\n")
            memberProfile != null -> memberProfile.sign
            else -> senderProfile.sign
        }

        val card = when (val type = match.groups[1]?.value) {
            "spell" -> {
                YgoCard.Spell(
                    name = name,
                    description = description,
                    face = face,
                    copyright = copyright
                )
            }
            "trap" -> {
                YgoCard.Trap(
                    name = name,
                    description = description,
                    face = face,
                    copyright = copyright
                )
            }
            "monster" -> {
                YgoCard.Monster(
                    name = name,
                    attribute = attribute,
                    level = level,
                    face = face,
                    race = race,
                    description = description,
                    attack = attack,
                    defend = defend,
                    copyright = copyright
                )
            }
            else -> throw IllegalArgumentException("card type: $type")
        }

        return card.render().makeSnapshotResource()
            .use { resource -> subject.uploadImage(resource = resource) }
    }
}