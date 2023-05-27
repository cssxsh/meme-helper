package xyz.cssxsh.mirai.meme

import kotlinx.serialization.*

@Serializable
public data class EmojiKitchenItem(
    @SerialName("leftEmoji")
    public val left: String,
    @SerialName("rightEmoji")
    public val right: String,
    @SerialName("date")
    public val date: String
) {
    public val filename: String
        get() =  "u${left.replace("-", "-u")}_u${right.replace("-", "-u")}.png"

    public val url: String
        get() = "https://www.gstatic.com/android/keyboard/emojikitchen/${date}/u${left.replace("-", "-u")}/${filename}"
}