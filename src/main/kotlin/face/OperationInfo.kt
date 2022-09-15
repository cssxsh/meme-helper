package xyz.cssxsh.mirai.meme.face

import kotlinx.serialization.*

@Serializable
public data class OperationInfo(
    @SerialName("isFree")
    @Serializable(with = NumberToBooleanSerializer::class)
    val isFree: Boolean,
    @SerialName("isShow")
    @Serializable(with = NumberToBooleanSerializer::class)
    val isShow: Boolean,
    @SerialName("limitFreeBeginTime")
    val limitFreeBeginTime: String,
    @SerialName("limitFreeEndTime")
    val limitFreeEndTime: String,
    @SerialName("maxVersion")
    val maxVersion: String,
    @SerialName("minVersion")
    val minVersion: String,
    @SerialName("platform")
    val platform: Int,
    @SerialName("price")
    val price: Double,
    @SerialName("productId")
    internal val productId: String,
    @SerialName("valid")
    @Serializable(with = NumberToBooleanSerializer::class)
    val valid: Boolean,
    @SerialName("validity")
    val validity: Int,
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