package xyz.cssxsh.mirai.meme.face

import kotlinx.serialization.*

@Serializable
public data class AdditionInfo(
    @SerialName("emojiId")
    val emojiId: String?,
    @SerialName("keyword")
    val keyword: String?,
    @SerialName("order")
    val order: Int,
    @SerialName("wordingText")
    internal val words: List<String> = emptyList(),
    @SerialName("fontcolor")
    internal val fontcolor: String = "",
    @SerialName("backcolor")
    internal val backcolor: String = "",
    @SerialName("heightItems")
    internal val heightItems: String = "",
    @SerialName("emojilabel")
    internal val emojiLabel: String = "",
    @SerialName("__v")
    internal val __v: Int = 0,
    @SerialName("cfgID")
    internal val cfgID: String = "",
    @SerialName("_id")
    internal val _id: String = "",
    @SerialName("id")
    internal val id: String = ""
)