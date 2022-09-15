package xyz.cssxsh.mirai.meme.face

import kotlinx.serialization.*

@Serializable
public data class AppData(
    @SerialName("name")
    val name: String
)