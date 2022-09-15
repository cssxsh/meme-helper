package xyz.cssxsh.mirai.meme.face

import kotlinx.serialization.*

@Serializable
public data class MD5Info(
    @SerialName("md5")
    val md5: String,
    @SerialName("name")
    val name: String
)