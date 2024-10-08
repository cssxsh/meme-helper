package xyz.cssxsh.mirai.meme.face

import kotlinx.serialization.*

/**
 * @property appId 1. 表情
 */
@Serializable
public data class ItemInfo(
    @SerialName("appid")
    val appId: Int = 0,
    @SerialName("itemid")
    val itemId: Int = 0,
    @SerialName("name")
    val name: String = "",
    @SerialName("onlinetime")
    val onlineTime: String = "",
    @SerialName("url")
    val url: String = ""
)