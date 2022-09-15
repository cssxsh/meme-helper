package xyz.cssxsh.mirai.meme.face

import kotlinx.serialization.*

@Serializable
public data class MarketFaceDetail(
    @SerialName("additionInfo")
    val addition: List<AdditionInfo>,
    @SerialName("baseInfo")
    val base: List<MarketFaceBaseInfo>,
    @SerialName("md5Info")
    val md5: List<MD5Info>,
    @SerialName("operationInfo")
    val operation: List<OperationInfo>,
    @SerialName("diyEmojiCommonText")
    val commons: List<String> = emptyList(),
    @SerialName("diversionConfig")
    val diversions: List<DiversionConfig> = emptyList(),
    @SerialName("__bgColors")
    internal val __bgColors: Map<String, BackgroundColor> = emptyMap(),
)