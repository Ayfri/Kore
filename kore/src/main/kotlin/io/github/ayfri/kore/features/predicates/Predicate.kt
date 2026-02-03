package io.github.ayfri.kore.features.predicates

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.inlinableListSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

typealias PredicateAsList = @Serializable(Predicate.Companion.PredicateAsListSerializer::class) Predicate

/**
 * Data-driven predicate definition for Minecraft Java Edition.
 *
 * A predicate is a JSON structure used in data packs to check conditions in the world,
 * returning pass or fail. Predicates are used in commands, loot tables, advancements,
 * and other data-driven features to filter or trigger actions based on game state.
 *
 * Kore serializes predicates as either a single object or an array of conditions,
 * matching the vanilla format. See the Minecraft Wiki for the full JSON structure and usage.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/predicates
 * JSON format reference: https://minecraft.wiki/w/Predicate
 */
@Serializable(with = Predicate.Companion.PredicateSerializer::class)
data class Predicate(
	override var fileName: String = "predicate",
	var predicateConditions: InlinableList<PredicateCondition> = emptyList(),
) : Generator("predicate") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)

	companion object {
		data object PredicateSerializer : KSerializer<Predicate> {
			override val descriptor = buildClassSerialDescriptor("Predicate")

			override fun deserialize(decoder: Decoder) = error("Predicate cannot be deserialized")

			override fun serialize(encoder: Encoder, value: Predicate) = encoder.encodeSerializableValue(
				inlinableListSerializer(PredicateCondition.serializer()),
				value.predicateConditions
			)
		}

		data object PredicateAsListSerializer : KSerializer<Predicate> by serializer() {
			override val descriptor = buildClassSerialDescriptor("PredicateAsList")

			override fun serialize(encoder: Encoder, value: Predicate) {
				encoder.encodeSerializableValue(
					ListSerializer(PredicateCondition.serializer()),
					value.predicateConditions
				)
			}
		}
	}
}

/**
 * Create and register a predicate in this [DataPack].
 *
 * Produces `data/<namespace>/predicate/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/predicates
 * JSON format reference: https://minecraft.wiki/w/Predicate
 */
fun DataPack.predicate(fileName: String = "predicate", init: Predicate.() -> Unit = {}): PredicateArgument {
	val predicate = Predicate(fileName).apply(init)
	predicates += predicate
	return PredicateArgument(fileName, predicate.namespace ?: name)
}
