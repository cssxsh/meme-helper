package xyz.cssxsh.mirai.meme.face

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
public data class MarketFaceBaseInfo(
    @SerialName("authorId")
    val authorId: Long,
    @SerialName("childMagicEmojiId")
    internal val childMagicEmojiId: String = "",
    @SerialName("desc")
    val description: String,
    @SerialName("favourite")
    @Serializable(with = NumberToBooleanSerializer::class)
    val favourite: Boolean = false,
    @SerialName("feeType")
    val feeType: Int,
    @SerialName("icon")
    val icon: Int,
    @SerialName("name")
    val name: String,
    @SerialName("providerId")
    val providerId: Long,
    @SerialName("qzone")
    internal val qzone: Int,
    @SerialName("realSize")
    val realSize: String,
    @SerialName("ringType")
    val ringType: Int,
    @SerialName("sex")
    val sex: Int = 0,
    @SerialName("sougou")
    internal val sougou: Int,
    @SerialName("tag")
    val tag: JsonElement,
    @SerialName("type")
    val type: Int,
    @SerialName("updateTipBeginTime")
    val updateTipBeginTime: String,
    @SerialName("updateTipEndTime")
    val updateTipEndTime: String,
    @SerialName("updateTipWording")
    val updateTipWording: String,
    @SerialName("valid")
    @Serializable(with = NumberToBooleanSerializer::class)
    val valid: Boolean,
    @SerialName("validArea")
    val validArea: Int,
    @SerialName("validBefore")
    val validBefore: String = "",
    @SerialName("isOriginal")
    @Serializable(NumberToBooleanSerializer::class)
    val isOriginal: Boolean = false,
    @SerialName("isApng")
    @Serializable(NumberToBooleanSerializer::class)
    val isAPNG: Boolean = false,
    @SerialName("QQgif")
    @Serializable(NumberToBooleanSerializer::class)
    val isGIF: Boolean = false,
    @SerialName("productId")
    val productId: String = "",
    @SerialName("label")
    val label: List<String> = emptyList(),
    @SerialName("zip")
    internal val zip: JsonElement,
    @SerialName("__v")
    internal val __v: Int = 0,
    @SerialName("cfgID")
    internal val cfgID: String = "",
    @SerialName("_id")
    internal val _id: String = "",
    @SerialName("id")
    internal val id: String = ""
)