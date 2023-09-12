package xyz.cssxsh.mirai.meme

import net.mamoe.mirai.utils.*

public data class EmojiKitchen internal constructor(val items: Map<String, List<EmojiKitchenItem>>) {

    public fun cook(left: String, right: String): Pair<String, String>? {
        val l = left.unicode()
        val r = right.unicode()
        val list = items[l] ?: items[r] ?: return null
        val item = list.find { it.left == l && it.right == r } ?: return null
        return item.filename to item.url
    }

    public companion object {
        @PublishedApi
        internal fun String.unicode(): String {
            val bytes = toByteArray(charset = Charsets.UTF_32)
            return (bytes.indices step 4).joinToString(separator = "-") {
                bytes.toInt(offset = it).toString(radix = 16)
            }
        }

        @PublishedApi
        internal const val LAST_UPDATE: String = "2023-09-09T17:21:15.000-07:00"

        // language=RegExp
        @PublishedApi
        internal const val EMOJI_REGEX: String = """\u2764\ufe0f\u200d\ud83e\ude79|\ud83d\ude2e\u200d\ud83d\udca8|\ud83d\ude36\u200d\ud83c\udf2b\ufe0f|[\u2601-\u2b50]\ufe0f?|[\ud83c\udc04-\ud83c\udff9]\ufe0f?|[\ud83d\udc0c-\ud83d\udefc]\ufe0f?|[\ud83e\udd0d-\ud83e\udee7]"""
    }
}