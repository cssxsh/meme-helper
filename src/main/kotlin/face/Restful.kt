package xyz.cssxsh.mirai.meme.face

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
internal data class Restful(
    @SerialName("data")
    val `data`: JsonElement,
    @SerialName("msg")
    val msg: String,
    @SerialName("ret")
    val ret: Int
)