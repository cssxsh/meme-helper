package xyz.cssxsh.mirai.meme.face

import kotlinx.serialization.*

@Serializable
public data class DiversionConfig(
    @SerialName("beginTime")
    val begin: Long = 0,
    @SerialName("copywriting")
    val copy: String = "",
    @SerialName("displayImg")
    val display: String = "",
    @SerialName("diversionName")
    val name: String = "",
    @SerialName("diversionType")
    val type: String = "",
    @SerialName("endTime")
    val end: Long = 0,
    @SerialName("from")
    val from: String = "",
    @SerialName("jumpUrl")
    val jump: String = "",
    @SerialName("wording")
    val wording: String = "",
    @SerialName("__v")
    internal val __v: Int = 0,
    @SerialName("cfgID")
    internal val cfgID: String = "",
    @SerialName("_id")
    internal val _id: String = "",
    @SerialName("id")
    internal val id: String = ""
)