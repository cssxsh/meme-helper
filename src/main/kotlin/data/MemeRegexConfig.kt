package xyz.cssxsh.mirai.meme.data

import kotlinx.serialization.modules.*
import net.mamoe.mirai.console.data.*
import xyz.cssxsh.mirai.meme.*

public object MemeRegexConfig : MemeRegex, AutoSavePluginConfig ("regex") {
    override val serializersModule: SerializersModule = SerializersModule {
        contextual(RegexSerializer)
    }
    override val random: Regex by value("表情包".toRegex())
    override val md5: Regex by value("""[0-9a-f]{32}""".toRegex())

    override val pornhub: Regex by value("""^#ph\s+(\S+)\s+(\S+)""".toRegex())
    override val petpet: Regex by value("""^#pet\s*(\d+)?""".toRegex())
    override val dear: Regex by value("""^#dear\s*(\d+)?""".toRegex())
    override val choyen: Regex by value("""^#choyen\s+(\S+)\s+(\S+)""".toRegex())
    override val zzkia: Regex by value("""^#(zzkia|pinyin)\s+((?s).+)""".toRegex())
}