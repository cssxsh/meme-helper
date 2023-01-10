package xyz.cssxsh.mirai.meme.face

import kotlinx.serialization.*

@Serializable
public data class OperationInfo(
    @SerialName("isFree")
    @Serializable(with = NumberToBooleanSerializer::class)
    val isFree: Boolean = true,
    @SerialName("isShow")
    @Serializable(with = NumberToBooleanSerializer::class)
    val isShow: Boolean = true,
    @SerialName("limitBeginTime")
    val limitBeginTime: String = "",
    @SerialName("limitEndTime")
    val limitEndTime: String = "",
    @SerialName("limitFreeBeginTime")
    val limitFreeBeginTime: String = "",
    @SerialName("limitFreeEndTime")
    val limitFreeEndTime: String = "",
    @SerialName("limitType")
    val limitType: Int = 0,
    @SerialName("maxVersion")
    val maxVersion: String,
    @SerialName("minVersion")
    val minVersion: String,
    @SerialName("platform")
    val platform: Int,
    @SerialName("price")
    val price: Double = 0.0,
    @SerialName("productId")
    val productId: String = "",
    @SerialName("valid")
    @Serializable(with = NumberToBooleanSerializer::class)
    val valid: Boolean = true,
    @SerialName("validity")
    val validity: Int = 0,
    @SerialName("whiteList")
    internal val white: String = "",
    @SerialName("__v")
    internal val __v: Int = 0,
    @SerialName("cfgID")
    internal val cfgID: String = "",
    @SerialName("_id")
    internal val _id: String = "",
    @SerialName("id")
    internal val id: String = ""
)