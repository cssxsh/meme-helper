package xyz.cssxsh.mirai.meme.face

import kotlinx.serialization.*
import kotlinx.serialization.builtins.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*

@PublishedApi
internal object KeywordsSerializer : KSerializer<List<String>> {

    override val descriptor: SerialDescriptor get() = JsonElement.serializer().descriptor

    override fun deserialize(decoder: Decoder): List<String> {
        return when (val element  = decoder.decodeSerializableValue(JsonElement.serializer())) {
            is JsonPrimitive -> listOf(element.content)
            is JsonArray -> element.map { it.jsonPrimitive.content }
            else -> throw SerializationException("data: $element")
        }
    }

    override fun serialize(encoder: Encoder, value: List<String>) {
        encoder.encodeSerializableValue(ListSerializer(String.serializer()), value)
    }
}