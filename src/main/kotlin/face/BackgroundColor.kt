package xyz.cssxsh.mirai.meme.face

import kotlinx.serialization.*

@Serializable
public data class BackgroundColor(
    @SerialName("code")
    val code: Int,
    @SerialName("color")
    val color: String
)