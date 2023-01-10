package xyz.cssxsh.mirai.meme.face

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
@OptIn(ExperimentalSerializationApi::class)
internal data class Restful(
    @JsonNames("data", "response")
    val data: JsonElement = JsonNull,
    @JsonNames("msg", "message")
    val message: String = "",
    @JsonNames("ret", "retCode")
    val ret: Int = 0,
    @SerialName("code")
    val code: Int = 200
)