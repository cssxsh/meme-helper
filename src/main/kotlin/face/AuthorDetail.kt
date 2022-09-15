package xyz.cssxsh.mirai.meme.face

import kotlinx.serialization.*

@Serializable
public data class AuthorDetail(
    @SerialName("authorName")
    val name: String,
    @SerialName("backgroundImg")
    val background: String,
    @SerialName("desc")
    val description: String,
    @SerialName("fansNum")
    val fans: Int,
    @SerialName("headImg")
    val head: String,
    @SerialName("isFollowed")
    @Serializable(with = NumberToBooleanSerializer::class)
    val isFollowed: Boolean,
    @SerialName("itemList")
    val items: List<MarketFaceItem>,
    @SerialName("itemNum")
    val total: Int,
    @SerialName("rewardDesc")
    val thank: String,
    @SerialName("rewardImg")
    val reward: String
)