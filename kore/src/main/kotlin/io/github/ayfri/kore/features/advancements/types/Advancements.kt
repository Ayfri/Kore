package io.github.ayfri.kore.features.advancements.types

import io.github.ayfri.kore.arguments.Advancement
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@Serializable(Advancements.Companion.AdvancementsSerializer::class)
data class Advancements(val advancements: Set<Advancement> = emptySet()) {
	companion object {
		object AdvancementsSerializer : KSerializer<Advancements> {
			override val descriptor = buildClassSerialDescriptor("Advancements")

			override fun deserialize(decoder: Decoder) = error("Advancements cannot be deserialized")

			override fun serialize(encoder: Encoder, value: Advancements) {
				require(encoder is JsonEncoder) { "Advancements can only be serialized as Json" }

				val jsonObject = buildJsonObject {
					value.advancements.forEach { (advancement, done, criteria) ->
						put(advancement.asId(), when {
							criteria.isNotEmpty() -> buildJsonObject {
								criteria.forEach { (key, value) -> put(key, value) }
							}

							else -> JsonPrimitive(done)
						})
					}
				}

				encoder.encodeJsonElement(jsonObject)
			}
		}
	}
}
