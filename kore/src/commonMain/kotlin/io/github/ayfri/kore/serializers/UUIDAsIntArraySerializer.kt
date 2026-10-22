package io.github.ayfri.kore.serializers

import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.IntArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.uuid.Uuid

/** Writes a [UUIDArgument] as the `int[4]` the game stores UUIDs as, `[I;...]` in SNBT and `[...]` in JSON. */
data object UUIDAsIntArraySerializer : KSerializer<UUIDArgument> {
	override val descriptor = SerialDescriptor("UUIDAsIntArray", IntArraySerializer().descriptor)

	override fun deserialize(decoder: Decoder): UUIDArgument {
		val (a, b, c, d) = decoder.decodeSerializableValue(IntArraySerializer())
		return UUIDArgument(
			Uuid.fromLongs(
				(a.toLong() shl 32) or (b.toLong() and 0xFFFFFFFFL),
				(c.toLong() shl 32) or (d.toLong() and 0xFFFFFFFFL),
			)
		)
	}

	override fun serialize(encoder: Encoder, value: UUIDArgument) =
		encoder.encodeSerializableValue(IntArraySerializer(), value.toIntArray())
}
