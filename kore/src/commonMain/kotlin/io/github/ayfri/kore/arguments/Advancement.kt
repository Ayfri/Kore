package io.github.ayfri.kore.arguments

import io.github.ayfri.kore.generated.arguments.types.AdvancementArgument
import io.github.ayfri.kore.serializers.decodeJsonObject
import io.github.ayfri.kore.serializers.splitNamespacedId
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable(Advancement.Companion.AdvancementSerializer::class)
data class Advancement(
	val advancement: AdvancementArgument,
	val done: Boolean = false,
	val criteria: Map<String, Boolean> = emptyMap(),
) {
	companion object {
		/** Builds an [Advancement] from a serialized `id` to `done | criteria` entry, as found in predicate JSON. */
		fun fromEntry(id: String, value: JsonElement): Advancement {
			val (name, namespace) = id.splitNamespacedId()
			val advancement = AdvancementArgument(name, namespace)
			return when (value) {
				is JsonObject -> Advancement(advancement, criteria = value.mapValues { it.value.jsonPrimitive.boolean })
				else -> Advancement(advancement, done = value.jsonPrimitive.boolean)
			}
		}

		data object AdvancementSerializer : KSerializer<Advancement> {
			override val descriptor = buildClassSerialDescriptor("Advancement")

			override fun deserialize(decoder: Decoder) =
				decoder.decodeJsonObject().entries.single().let { (id, value) -> fromEntry(id, value) }

			override fun serialize(encoder: Encoder, value: Advancement) {
				require(encoder is JsonEncoder) { "Advancement can only be serialized as Json" }

				val jsonObject = buildJsonObject {
					put(value.advancement.asId(), when {
						value.criteria.isNotEmpty() -> buildJsonObject {
							value.criteria.forEach { (key, value) -> put(key, value) }
						}

						else -> JsonPrimitive(value.done)
					})
				}

				encoder.encodeJsonElement(jsonObject)
			}
		}
	}
}
