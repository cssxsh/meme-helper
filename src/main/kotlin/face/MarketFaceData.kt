package xyz.cssxsh.mirai.meme.face

import kotlinx.serialization.*

@Serializable
public data class MarketFaceData(
    @SerialName("appData")
    val app: AppData,
    @SerialName("data")
    val detail: MarketFaceDetail,
    @SerialName("timestamp")
    val timestamp: Long? = null
)