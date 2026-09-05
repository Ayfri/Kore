package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.arguments.Advancement
import io.github.ayfri.kore.serializers.decodeJsonObject
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Matches the advancement progress of a player, as the `advancements` key of a [PlayerSubPredicate].
 *
 * Each [Advancement] is written as an advancement id mapped either to a boolean (whether the whole advancement is
 * granted) or to an object of criterion name to boolean when individual criteria are listed.
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@Serializable(AdvancementsPredicate.Companion.AdvancementsPredicateSerializer::class)
data class AdvancementsPredicate(val advancements: Set<Advancement> = emptySet()) {
	companion object {
		data object AdvancementsPredicateSerializer : KSerializer<AdvancementsPredicate> {
			override val descriptor = buildClassSerialDescriptor("AdvancementsPredicate")

			override fun deserialize(decoder: Decoder) =
				AdvancementsPredicate(decoder.decodeJsonObject().map { (id, value) -> Advancement.fromEntry(id, value) }.toSet())

			override fun serialize(encoder: Encoder, value: AdvancementsPredicate) {
				require(encoder is JsonEncoder) { "AdvancementsPredicate can only be serialized as Json" }

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
