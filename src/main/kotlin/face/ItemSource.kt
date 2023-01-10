package xyz.cssxsh.mirai.meme.face

import kotlinx.serialization.*

@Serializable
public data class ItemSource(
    @SerialName("cdnName")
    val cdnName: String = "",
    @SerialName("extName")
    val extName: String = "",
    @SerialName("from")
    val from: String = "",
    @SerialName("hashName")
    val hashName: String = "",
    @SerialName("md5")
    val md5: String = "",
    @SerialName("size")
    val size: Int = 0,
    @SerialName("src")
    val src: String = "",
    @SerialName("subPath")
    val subPath: String = ""
)