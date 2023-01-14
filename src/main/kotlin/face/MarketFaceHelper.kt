package xyz.cssxsh.mirai.meme.face

import io.ktor.client.call.*
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
import java.util.*

@MiraiExperimentalApi
@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
public object MarketFaceHelper {
    @PublishedApi
    internal val json: Json = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }
    @PublishedApi
    internal val authors: MutableMap<Long, AuthorDetail> = WeakHashMap()
    @PublishedApi
    internal val suppliers: MutableMap<Long, SupplierInfo> = WeakHashMap()
    @PublishedApi
    internal val relations: MutableMap<Int, RelationIdInfo> = WeakHashMap()
    @PublishedApi
    internal val items: MutableMap<Int, ItemData> = WeakHashMap()
    @PublishedApi
    internal val faces: MutableMap<Int, MarketFaceData> = WeakHashMap()
    @PublishedApi
    internal val faces2: MutableMap<Int, MarketFaceAndroid> = WeakHashMap()
    @PublishedApi
    internal val defaultPbReserve: ByteArray = "0A 06 08 AC 02 10 AC 02 0A 06 08 C8 01 10 C8 01 40 01".hexToBytes()

    public suspend fun queryAuthorDetail(authorId: Long): AuthorDetail {
        val cache = authors[authorId]
        if (cache != null) return cache

        val bot = Bot.instances.randomOrNull() ?: throw IllegalStateException("No Bot Instance")
        bot as QQAndroidBot
        val text = http.get("https://open.vip.qq.com/open/getAuthorDetail") {
            parameter("authorId", authorId)
            parameter("g_tk", bot.client.wLoginSigInfo.bkn)

            headers {
                append(
                    "cookie",
                    "uin=o${bot.id}; skey=${bot.sKey}; p_uin=o${bot.id}; p_skey=${bot.psKey("vip.qq.com")};"
                )
            }
        }.bodyAsText()

        val result = Json.decodeFromString(Restful.serializer(), text)

        check(result.ret == 0) { result.message }

        val detail = json.decodeFromJsonElement(AuthorDetail.serializer(), result.data)
        authors[authorId] = detail
        return detail
    }

    public suspend fun queryRelationId(itemId: Int): RelationIdInfo {
        val cache = relations[itemId]
        if (cache != null) return cache

        val bot = Bot.instances.randomOrNull() ?: throw IllegalStateException("No Bot Instance")
        bot as QQAndroidBot
        val text = http.get("https://open.vip.qq.com/open/getRelationId") {
            parameter("appId", "1")
            parameter("adminItemId", itemId)
            parameter("g_tk", bot.client.wLoginSigInfo.bkn)

            headers {
                append(
                    "cookie",
                    "uin=o${bot.id}; skey=${bot.sKey}; p_uin=o${bot.id}; p_skey=${bot.psKey("vip.qq.com")};"
                )
            }
        }.bodyAsText()

        val result = Json.decodeFromString(Restful.serializer(), text)

        check(result.ret == 0) { result.message }
        val info = json.decodeFromJsonElement(RelationIdInfo.serializer(), result.data)
        relations[itemId] = info
        return info
    }

    public suspend fun queryRelationId(face: MarketFace): RelationIdInfo = queryRelationId(itemId = face.id)

    public suspend fun queryFaceDetail(itemId: Int): MarketFaceData {
        val cache = faces[itemId]
        if (cache != null) return cache

        val text = http.get("https://gxh.vip.qq.com/qqshow/admindata/comdata/vipEmoji_item_${itemId}/xydata.json")
            .bodyAsText()

        val data = json.decodeFromString(MarketFaceData.serializer(), text)
        faces[itemId] = data
        return data
    }

    public suspend fun queryFaceDetail(face: MarketFace): MarketFaceData = queryFaceDetail(itemId = face.id)

    public suspend fun queryFaceAndroid(itemId: Int): MarketFaceAndroid {
        val cache = faces2[itemId]
        if (cache != null) return cache

        val text = http.get("https://gxh.vip.qq.com/club/item/parcel/${itemId % 10}/${itemId}_android.json")
            .bodyAsText()

        val data = json.decodeFromString(MarketFaceAndroid.serializer(), text)
        faces2[itemId] = data
        return data
    }

    public suspend fun queryFaceAndroid(face: MarketFace): MarketFaceAndroid = queryFaceAndroid(itemId = face.id)

    public suspend fun queryItemData(itemId: Int): ItemData {
        val cache = items[itemId]
        if (cache != null) return cache

        val bot = Bot.instances.randomOrNull() ?: throw IllegalStateException("No Bot Instance")
        bot as QQAndroidBot
        val text = http.get("https://zb.vip.qq.com/v2/home/cgi/getItemData") {
            parameter("bid", 1)
            parameter("id", itemId)
            parameter("g_tk", bot.client.wLoginSigInfo.bkn)

            headers {
                append(
                    "cookie",
                    "uin=o${bot.id}; skey=${bot.sKey}; p_uin=o${bot.id}; p_skey=${bot.psKey("vip.qq.com")};"
                )
            }
        }.bodyAsText()

        val result = Json.decodeFromString(Restful.serializer(), text)

        check(result.ret == 0) { result.message }
        val data = json.decodeFromJsonElement(ItemData.serializer(), result.data)
        items[itemId] = data
        return data
    }

    public suspend fun queryItemData(face: MarketFace): ItemData = queryItemData(itemId = face.id)

    public suspend fun querySupplierInfo(supplierId: Long, offset: Int): SupplierInfo {
        val cache = suppliers[supplierId]
        if (cache != null) return cache

        val bot = Bot.instances.randomOrNull() ?: throw IllegalStateException("No Bot Instance")
        bot as QQAndroidBot
        val text = http.post {
            url("https://zb.vip.qq.com/trpc-proxy/qc/supplyerservice/Supplyer/SupplyerInfo")

            setBody(buildJsonObject {
                putJsonObject("req") {
                    put("supplyerid", supplierId)
                    // offset
                    put("nextID", offset)
                    // page size
                    put("Pagesize", 30)
                    putJsonObject("stlogin") {
                        put("ikeytype", 1)
                        put("iKeyType", 1)
                        put("iopplat", 2)
                        put("iOpplat", 2)
                        put("sClientIp", "")
                        put("sclientver", "8.9.28")
                        put("sClientVer", "8.9.28")
                        put("skey", bot.sKey)
                        put("sSkey", bot.sKey)
                        put("lLoginInfo", "")
                    }
                }
                putJsonObject("options") {
                    putJsonObject("context") {
                        put("businessType", "qqgxh")
                    }
                    putJsonObject("naming") {
                        put("namespace", "Production")
                        put("env", "formal")
                    }
                }
            }.toString())
            contentType(ContentType.Application.Json)

            headers {
                append(
                    "cookie",
                    "uin=o${bot.id}; skey=${bot.sKey}; p_uin=o${bot.id}; p_skey=${bot.psKey("vip.qq.com")};"
                )
            }
        }.bodyAsText()

        val result = Json.decodeFromString(Restful.serializer(), text)

        check(result.ret == 0) { result.message }
        val info = json.decodeFromJsonElement(SupplierInfo.serializer(), result.data)
        suppliers[supplierId] = info
        return info
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
                faceInfo = info.feeType,
                faceId = md5.hexToBytes(),
                tabId = itemId,
                subType = info.type,
                key = key,
                imageWidth = 200,
                imageHeight = 200,
                pbReserve = defaultPbReserve
            )

            MarketFaceImpl(delegate = delegate)
        }
    }

    public fun build(data: MarketFaceAndroid): List<MarketFace> {
        val key = data.updateTime.toString().md5()
            .toUHexString("")
            .lowercase().substring(0, 16)
            .toByteArray()

        return data.images.map { image ->
            val delegate = ImMsgBody.MarketFace(
                faceName = "[${image.name}]".toByteArray(),
                itemType = 6,
                faceInfo = data.feeType.toInt(),
                faceId = image.id.hexToBytes(),
                tabId = data.id.toInt(),
                subType = data.type,
                key = key,
                imageWidth = image.width,
                imageHeight = image.height,
                pbReserve = defaultPbReserve
            )

            MarketFaceImpl(delegate = delegate)
        }
    }

    public suspend fun source(impl: MarketFace): ByteArray {
        impl as MarketFaceImpl
        val md5 = impl.delegate.faceId.toUHexString("").lowercase()
        val size = if (impl.delegate.tabId < 10_0000) 200 else 300
        val url = when (impl.delegate.subType) {
            1 -> "https://gxh.vip.qq.com/club/item/parcel/item/${md5.substring(0, 2)}/$md5/raw${size}.gif"
            2 -> "https://gxh.vip.qq.com/club/item/parcel/item/${md5.substring(0, 2)}/$md5/raw${size}.png"
            3 -> "https://gxh.vip.qq.com/club/item/parcel/item/${md5.substring(0, 2)}/$md5/raw${size}.gif"
            else -> "https://gxh.vip.qq.com/club/item/parcel/item/${md5.substring(0, 2)}/$md5/${size}x${size}.png"
        }

        return http.get(url).body()
    }
}