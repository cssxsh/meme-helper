package xyz.cssxsh.mirai.meme

import kotlinx.serialization.*

@Serializable
public data class EmojiKitchenCombination(
    @SerialName("gStaticUrl")
    public val gStaticUrl: String,
    @SerialName("alt")
    public val alt: String,
    @SerialName("leftEmoji")
    public val leftEmoji: String,
    @SerialName("leftEmojiCodepoint")
    public val leftEmojiCodepoint: String,
    @SerialName("rightEmoji")
    public val rightEmoji: String,
    @SerialName("rightEmojiCodepoint")
    public val rightEmojiCodepoint: String,
    @SerialName("date")
    public val date: String,
    @SerialName("isLatest")
    public val isLatest: Boolean,
    @SerialName("gBoardOrder")
    public val gBoardOrder: Int,
) {
    public val filename: String
        get() = gStaticUrl.substringAfterLast('/')

}