package xyz.cssxsh.mirai.meme

import net.mamoe.mirai.utils.*

public data class EmojiKitchen(public val urls: Map<String, String>) {

    public fun cook(emojis: Sequence<String>): Pair<String, String>? {
        val filename = emojis.joinToString(separator = "_", postfix = ".png") { it.unicode() }
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

        // language=RegExp
        internal const val REGEX = """[\u2601-\u2b50][\ufe0f]?|[\ud83c\udc04-\ud83c\udff9][\ufe0f]?|[\ud83d\udc0c-\ud83d\udefc][\ufe0f]?|[\ud83e\udd0d-\ud83e\udee7]"""
    }
}