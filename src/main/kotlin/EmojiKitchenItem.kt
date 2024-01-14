package xyz.cssxsh.mirai.meme

import kotlinx.serialization.*

@Serializable
public data class EmojiKitchenItem(
    @SerialName("alt")
    public val alt: String,
    @SerialName("emoji")
    public val emoji: String,
    @SerialName("emojiCodepoint")
    public val emojiCodepoint: String,
    @SerialName("gBoardOrder")
    public val gBoardOrder: Int,
    @SerialName("keywords")
    public val keywords: List<String>,
    @SerialName("category")
    public val category: String,
    @SerialName("subcategory")
    public val subcategory: String,
    @SerialName("combinations")
    public val combinations: List<EmojiKitchenCombination>,
)