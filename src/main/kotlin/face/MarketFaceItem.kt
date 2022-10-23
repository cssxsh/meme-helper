package xyz.cssxsh.mirai.meme.face

import kotlinx.serialization.*

@Serializable
public data class MarketFaceItem(
    @SerialName("appId")
    val appId: Long,
    @SerialName("desc")
    val description: String,
    @SerialName("feeType")
    val feeType: Int,
    @SerialName("itemId")
    val itemId: Int,
    @SerialName("itemName")
    val name: String,
    @SerialName("thumbImg")
    val thumb: String
) {
    public val url: String by lazy { "https://zb.vip.qq.com/hybrid/emoticonmall/detail?id=${itemId}" }
}