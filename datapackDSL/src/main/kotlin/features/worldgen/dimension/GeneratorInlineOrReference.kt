package features.worldgen.dimension

import Generator
import generated.NoiseSettings
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer

@Serializable(with = GeneratorInlineOrReference.Companion.GeneratorInlineOrReferenceSerializer::class)
data class GeneratorInlineOrReference(
	val reference: NoiseSettings? = null,
	val inline: Generator? = null,
) {
	companion object {
		object GeneratorInlineOrReferenceSerializer : KSerializer<GeneratorInlineOrReference> {
			override val descriptor = buildClassSerialDescriptor("GeneratorInlineOrReference") {
				element<String>("type")
			}

			override fun deserialize(decoder: Decoder) = error("GeneratorInlineOrReference is not deserializable")

			override fun serialize(encoder: Encoder, value: GeneratorInlineOrReference) = when {
				value.reference != null -> encoder.encodeSerializableValue(serializer<NoiseSettings>(), value.reference)
				value.inline != null -> encoder.encodeSerializableValue(serializer<Generator>(), value.inline)
				else -> error("GeneratorInlineOrReference must have either a reference or an inline value")
			}
		}
	}
}
