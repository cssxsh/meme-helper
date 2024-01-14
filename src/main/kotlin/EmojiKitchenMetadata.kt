package xyz.cssxsh.mirai.meme

import kotlinx.serialization.*

@Serializable
public data class EmojiKitchenMetadata(
    @SerialName("knownSupportedEmoji")
    public val knownSupportedEmoji: List<String>,
    @SerialName("data")
    public val data: Map<String, EmojiKitchenItem>
)