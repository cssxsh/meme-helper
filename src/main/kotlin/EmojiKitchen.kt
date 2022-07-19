package xyz.cssxsh.mirai.meme

import net.mamoe.mirai.utils.*

public data class EmojiKitchen(public val urls: Map<String, String>) {

    public fun cook(first: String, second: String): Pair<String, String>? {
        val filename = "${first.unicode()}_${second.unicode()}.png"
        logger.debug { filename }
        val url = urls[filename]
        if (url != null) {
            return filename to url
        }
        return null
    }

    public companion object {
        internal fun String.unicode(): String {
            val bytes = toByteArray(charset = Charsets.UTF_32)
            return (bytes.indices step 4).joinToString(separator = "-") {
                'u' + bytes.toInt(offset = it).toString(radix = 16)
            }
        }

        internal const val LAST_UPDATE = "2022-03-11T23:19:31Z"

        // language=RegExp
        internal const val EMOJI_REGEX = """\u2764\ufe0f\u200d\ud83e\ude79|\ud83d\ude2e\u200d\ud83d\udca8|\ud83d\ude36\u200d\ud83c\udf2b\ufe0f|[\u2601-\u2b50][\ufe0f]?|[\ud83c\udc04-\ud83c\udff9][\ufe0f]?|[\ud83d\udc0c-\ud83d\udefc][\ufe0f]?|[\ud83e\udd0d-\ud83e\udee7]"""
    }
}