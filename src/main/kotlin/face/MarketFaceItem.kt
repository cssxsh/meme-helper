package xyz.cssxsh.mirai.meme.face

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
)