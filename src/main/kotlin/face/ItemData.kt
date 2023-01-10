package xyz.cssxsh.mirai.meme.face

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import net.mamoe.mirai.utils.*

@Serializable
public data class ItemData(
    @SerialName("authorId")
    val authorId: Int = 0,
    @SerialName("bid")
    val bid: String = "",
    @SerialName("cfgID")
    internal val cfgID: String = "",
    @SerialName("childMagicEmojiId")
    internal val childMagicEmojiId: String = "",
    @SerialName("desc")
    val description: String = "",
    @SerialName("favourite")
    val favourite: Int = 0,
    @SerialName("feeType")
    val feeType: Int = 0,
    @SerialName("icon")
    val icon: Int = 0,
    @SerialName("_id")
    internal val _id: String = "",
    @SerialName("id")
    internal val id: String = "",
    @SerialName("isApng")
    @Serializable(with = NumberToBooleanSerializer::class)
    val isApng: Boolean = false,
    @SerialName("isFree")
    @Serializable(with = NumberToBooleanSerializer::class)
    val isFree: Boolean = false,
    @SerialName("isOriginal")
    @Serializable(with = NumberToBooleanSerializer::class)
    val isOriginal: Boolean = false,
    @SerialName("QQgif")
    @Serializable(with = NumberToBooleanSerializer::class)
    val isQQgif: Boolean = false,
    @SerialName("isShow")
    @Serializable(with = NumberToBooleanSerializer::class)
    val isShow: Boolean = false,
    @SerialName("itemBgcolor")
    val itemBgColor: String = "",
    @SerialName("itemId")
    val itemId: String = "",
    @SerialName("itemImg")
    val itemImage: String = "",
    @SerialName("itemTitle")
    val itemTitle: String = "",
    @SerialName("itemType")
    val itemType: String = "",
    @SerialName("label")
    val label: List<String> = emptyList(),
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
    val maxVersion: String = "",
    @SerialName("minVersion")
    val minVersion: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("platform")
    val platform: Int = 0,
    @SerialName("price")
    val price: Double = 0.0,
    @SerialName("productId")
    val productId: String = "",
    @SerialName("providerId")
    val providerId: Int = 0,
    @SerialName("qzone")
    val qzone: Int = 0,
    @SerialName("realSize")
    val realSize: String = "",
    @SerialName("ringType")
    val ringType: Int = 0,
    @SerialName("sex")
    val sex: Int = 0,
    @SerialName("sougou")
    val sougou: Int = 0,
    @SerialName("tag")
    val tag: JsonElement = JsonNull,
    @SerialName("tagType")
    val tagType: Int = 0,
    @SerialName("type")
    val type: Int = 0,
    @SerialName("typeName")
    val typeName: String = "",
    @SerialName("updateTipBeginTime")
    val updateTipBeginTime: Int = 0,
    @SerialName("updateTipEndTime")
    val updateTipEndTime: Int = 0,
    @SerialName("updateTipWording")
    val updateTipWording: String = "",
    @SerialName("valid")
    @Serializable(with = NumberToBooleanSerializer::class)
    val valid: Boolean,
    @SerialName("validArea")
    val validArea: Int = 0,
    @SerialName("validBefore")
    val validBefore: String = "",
    @SerialName("validity")
    val validity: Int = 0,
    @SerialName("whiteList")
    val whiteList: String = "",
    @SerialName("pc")
    val operation: List<OperationInfo> = emptyList(),
    @SerialName("zip")
    val zip: JsonElement = JsonNull
) {
    @OptIn(MiraiExperimentalApi::class)
    public val compressed: ItemSource? by lazy {
        val map: Map<String, ItemSource> = try {
            MarketFaceHelper.json.decodeFromJsonElement(zip)
        } catch (_: SerializationException) {
            emptyMap()
        }
        map["compressed"]
    }

    @OptIn(MiraiExperimentalApi::class)
    public val src: ItemSource? by lazy {
        val map: Map<String, ItemSource> = try {
            MarketFaceHelper.json.decodeFromJsonElement(zip)
        } catch (_: SerializationException) {
            emptyMap()
        }
        map["src"]
    }
}