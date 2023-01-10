package xyz.cssxsh.mirai.meme.face

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
public data class SupplierInfo(
    @SerialName("accountLevel")
    val accountLevel: Int = 0,
    @SerialName("commrsp")
    val comm: JsonElement = JsonNull,
    @SerialName("dyhAppid")
    val appId: String = "",
    @SerialName("fansnum")
    val fansNum: Int = 0,
    @SerialName("isfance")
    @Serializable(with = NumberToBooleanSerializer::class)
    val isFans: Boolean = false,
    @SerialName("nextID")
    val offset: Int = 0,
    @SerialName("puin")
    val uin: String = "",
    @SerialName("qqGroup")
    val group: Long = 0,
    @SerialName("rptOpenitem")
    val items: List<ItemInfo> = emptyList(),
    @SerialName("supplyerdes")
    val description: String = "",
    @SerialName("supplyerface")
    val face: String = "",
    @SerialName("supplyername")
    val name: String = "",
    @SerialName("worknum")
    val workNum: Int = 0
)