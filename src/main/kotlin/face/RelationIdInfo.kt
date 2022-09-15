package xyz.cssxsh.mirai.meme.face

import kotlinx.serialization.*

@Serializable
public data class RelationIdInfo(
    @SerialName("authorHeadImg")
    val head: String,
    @SerialName("authorId")
    val authorId: Long,
    @SerialName("authorName")
    val name: String,
    @SerialName("openItemId")
    val openItemId: Long
)