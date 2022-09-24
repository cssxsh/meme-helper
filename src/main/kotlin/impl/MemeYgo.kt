package xyz.cssxsh.mirai.meme.impl

import kotlinx.coroutines.*
import net.mamoe.mirai.console.permission.*
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.mirai.meme.*
import xyz.cssxsh.mirai.meme.service.*
import xyz.cssxsh.mirai.skia.*
import java.io.File
import java.util.*
import java.util.zip.*

/**
 * [游戏王制卡器](https://ymssx.github.io/ygo/#/)
 * @see YgoCard
 */
public class MemeYgo : MemeService {
    override val name: String = "Ygo"
    override val id: String = "ygo"
    override val description: String = "Ygo 卡片 生成器"
    override val loaded: Boolean = true
    override var regex: Regex = """^#(spell|trap|monster)\s*(\d+)?""".toRegex()
        private set
    override val properties: Properties = Properties().apply { put("regex", regex.pattern) }
    override lateinit var permission: Permission
    private lateinit var loadJob: Job

    override fun load(folder: File) {
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
        }
    }

    override fun enable(permission: Permission) {
        this.permission = permission
        runBlocking {
            loadJob.join()
        }
    }

    override fun disable() {}

    private fun MutableList<String>.push(key: String): String? {
        var temp: String? = null
        removeAll { line ->
            if (line.startsWith(key) && line.getOrNull(key.length) == '=') {
                temp = line
                true
            } else {
                false
            }
        }
        return temp?.removePrefix(key)?.removePrefix("=")
    }

    override suspend fun MessageEvent.replier(match: MatchResult): Image {
        val id = match.groups[2]?.value?.toLongOrNull()
            ?: message.findIsInstance<At>()?.target
            ?: sender.id
        val image = message.findIsInstance<Image>()
        val face = when {
            image != null -> cache(image = image)
            else -> avatar(id = id, size = 640)
        }
        val lines = message.filterIsInstance<PlainText>().last().content
            .lineSequence().toMutableList()
        val member = (subject as? Group)?.get(id = id)
        val profile = (member ?: sender).queryProfile()
        // XXX: handle command
        lines.removeAll { it.startsWith('#') }
        val name = lines.push(key = "name")
            ?: member?.remarkOrNameCardOrNick
            ?: sender.nameCardOrNick
        val attribute = lines.push(key = "attr")
            ?.let { YgoCard.Attribute.valueOf(it) }
            ?: YgoCard.Attribute.monster.random()
        val level = lines.push(key = "level")?.toInt() ?: (profile.qLevel / 16 + 1)
        val race = lines.push(key = "race")?.split(',', ' ', '/') ?: listOfNotNull(
            member?.specialTitle?.takeUnless { it.isBlank() },
            member?.permission?.name,
            profile.sex.name
        )
        val attack = lines.push(key = "atk") ?: "0"
        val defend = lines.push(key = "def") ?: "0"
        val copyright = lines.push(key = "copyright")
        val description = when {
            lines.isNotEmpty() -> lines.joinToString(separator = "\n")
            else -> profile.sign
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