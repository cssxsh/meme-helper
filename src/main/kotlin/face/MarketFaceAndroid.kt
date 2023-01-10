package xyz.cssxsh.mirai.meme.face

import kotlinx.serialization.*

@Serializable
public data class MarketFaceAndroid(
    @SerialName("author")
    val author: String = "",
    @SerialName("childEmojiId")
    internal val childEmojiId: String = "",
    @SerialName("commDiyText")
    val commDiyText: List<String> = emptyList(),
    @SerialName("downloadcount")
    val downloadCount: Int = 0,
    @SerialName("feetype")
    val feeType: String = "",
    @SerialName("filesize")
    val fileSize: String = "",
    @SerialName("id")
    val id: String = "",
    @SerialName("imgs")
    val images: List<Image> = emptyList(),
    @SerialName("isApng")
    @Serializable(with = NumberToBooleanSerializer::class)
    val isApng: Boolean = false,
    @SerialName("isOriginal")
    @Serializable(with = NumberToBooleanSerializer::class)
    val isOriginal: Boolean = false,
    @SerialName("mark")
    val mark: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("operationInfo")
    val operation: List<OperationInfo> = emptyList(),
    @SerialName("price")
    val price: Double = 0.0,
    @SerialName("rights")
    val rights: String = "",
    @SerialName("ringtype")
    val ringType: String = "",
    @SerialName("status")
    val status: String = "",
    @SerialName("supportApngSize")
    val supportApngSize: List<Size> = emptyList(),
    @SerialName("supportSize")
    val supportSize: List<Size> = emptyList(),
    @SerialName("type")
    val type: Int = 0,
    @SerialName("updateTime")
    val updateTime: Long = 0,
    @SerialName("validArea")
    val validArea: String = ""
) {
    @Serializable
    public data class Image(
        @SerialName("diyText")
        val diyText: List<String> = emptyList(),
        @SerialName("id")
        val id: String = "",
        @SerialName("keywords")
        val keywords: List<String> = emptyList(),
        @SerialName("name")
        val name: String = "",
        @SerialName("param")
        val param: String = "",
        @SerialName("wHeightInPhone")
        val wHeightInPhone: Int = 0,
        @SerialName("wWidthInPhone")
        val wWidthInPhone: Int = 0
    )

    @Serializable
    public data class Size(
        @SerialName("Height")
        val height: Int = 0,
        @SerialName("Width")
        val width: Int = 0
    )
}