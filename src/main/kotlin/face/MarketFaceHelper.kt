package xyz.cssxsh.mirai.meme.face

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*
import net.mamoe.mirai.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import net.mamoe.mirai.internal.*
import net.mamoe.mirai.internal.network.*
import net.mamoe.mirai.internal.message.data.*
import net.mamoe.mirai.internal.network.protocol.data.proto.*
import xyz.cssxsh.mirai.meme.*

@MiraiExperimentalApi
@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
public object MarketFaceHelper {
    internal val json: Json = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }

    public suspend fun queryAuthorDetail(authorId: Long): AuthorDetail {
        val bot = Bot.instances.randomOrNull() ?: throw IllegalStateException("No Bot Instance")
        bot as QQAndroidBot
        val text = http.get("https://open.vip.qq.com/open/getAuthorDetail") {
            parameter("authorId", authorId)
            parameter("g_tk", bot.client.wLoginSigInfo.bkn)

            headers {
                // ktor bug
                append(
                    "cookie",
                    "uin=o${bot.id}; skey=${bot.sKey}; p_uin=o${bot.id}; p_skey=${bot.psKey("vip.qq.com")};"
                )
            }
        }.bodyAsText()

        val result = Json.decodeFromString(Restful.serializer(), text)

        check(result.ret == 0) { result.msg }

        return json.decodeFromJsonElement(AuthorDetail.serializer(), result.data)
    }

    public suspend fun queryRelationId(itemId: Int): RelationIdInfo {
        val bot = Bot.instances.randomOrNull() ?: throw IllegalStateException("No Bot Instance")
        bot as QQAndroidBot
        val text = http.get("https://open.vip.qq.com/open/getRelationId") {
            parameter("appId", "1")
            parameter("adminItemId", itemId)
            parameter("g_tk", bot.client.wLoginSigInfo.bkn)

            headers {
                // ktor bug
                append(
                    "cookie",
                    "uin=o${bot.id}; skey=${bot.sKey}; p_uin=o${bot.id}; p_skey=${bot.psKey("vip.qq.com")};"
                )
            }
        }.bodyAsText()

        val result = Json.decodeFromString(Restful.serializer(), text)

        check(result.ret == 0) { result.msg }

        return json.decodeFromJsonElement(RelationIdInfo.serializer(), result.data)
    }

    public suspend fun queryRelationId(face: MarketFace): RelationIdInfo = queryRelationId(itemId = face.id)

    public suspend fun queryFaceDetail(itemId: Int): MarketFaceData {
        val text = http.get("https://gxh.vip.qq.com/qqshow/admindata/comdata/vipEmoji_item_209583/xydata.json") {
            url {
                encodedPath = encodedPath.replace("209583", itemId.toString())
            }
        }.bodyAsText()

        return json.decodeFromString(MarketFaceData.serializer(), text)
    }

    public suspend fun queryFaceDetail(face: MarketFace): MarketFaceData = queryFaceDetail(itemId = face.id)

    public suspend fun queryItemData(appId: Int, itemId: Int): String {
        val bot = Bot.instances.randomOrNull() ?: throw IllegalStateException("No Bot Instance")
        bot as QQAndroidBot
        val text = http.get("https://zb.vip.qq.com/v2/home/cgi/getItemData") {
            parameter("bid", appId)
            parameter("id", itemId)
            parameter("g_tk", bot.client.wLoginSigInfo.bkn)

            headers {
                // ktor bug
                append(
                    "cookie",
                    "uin=o${bot.id}; skey=${bot.sKey}; p_uin=o${bot.id}; p_skey=${bot.psKey("vip.qq.com")};"
                )
            }
        }.bodyAsText()

        return text
    }

    public fun build(itemId: Int, data: MarketFaceData): List<MarketFace> {
        val info = data.detail.base.single()
        val timestamp = requireNotNull(data.timestamp) { "Not Found Timestamp" } / 1_000
        val key = timestamp.toString().md5()
            .toUHexString("")
            .lowercase().substring(0, 16)
            .toByteArray()

        return data.detail.md5.map { (md5, name) ->
            val delegate = ImMsgBody.MarketFace(
                faceName = "[$name]".toByteArray(),
                itemType = 6,
                faceInfo = info.type,
                faceId = md5.hexToBytes(),
                tabId = itemId,
                subType = info.ringType,
                key = key,
                mediaType = 0,
                imageWidth = 200,
                imageHeight = 200
            )

            MarketFaceImpl(delegate = delegate)
        }
    }
}