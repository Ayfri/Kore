package io.github.ayfri.kore.arguments

import io.github.ayfri.kore.arguments.types.resources.AdvancementArgument
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@Serializable(Advancement.Companion.AdvancementSerializer::class)
data class Advancement(
	val advancement: AdvancementArgument,
	val done: Boolean = false,
	val criteria: Map<String, Boolean> = emptyMap(),
) {
	companion object {
		object AdvancementSerializer : KSerializer<Advancement> {
			override val descriptor = buildClassSerialDescriptor("Advancement")

			override fun deserialize(decoder: Decoder) = error("Advancement cannot be deserialized")

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
