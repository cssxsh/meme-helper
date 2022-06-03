package xyz.cssxsh.mirai.meme.data

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

internal object RegexSerializer : KSerializer<Regex> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Regex", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Regex = Regex(pattern = decoder.decodeString())

    override fun serialize(encoder: Encoder, value: Regex) = encoder.encodeString(value = value.pattern)
}