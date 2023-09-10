package io.github.ayfri.kore.features.predicates

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.resources.PredicateArgument
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.inlinableListSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer

typealias PredicateAsList = @Serializable(Predicate.Companion.PredicateAsListSerializer::class) Predicate

@Serializable(with = Predicate.Companion.PredicateSerializer::class)
data class Predicate(
	override var fileName: String = "predicate",
	var predicateConditions: InlinableList<PredicateCondition> = emptyList(),
) : Generator("predicate") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(predicateConditions)

	companion object {
		object PredicateSerializer : KSerializer<Predicate> {
			override val descriptor = buildClassSerialDescriptor("Predicate")

			override fun deserialize(decoder: Decoder) = error("Predicate cannot be deserialized")

			override fun serialize(encoder: Encoder, value: Predicate) {
				encoder.encodeSerializableValue(
					inlinableListSerializer(serializer<PredicateCondition>()),
					value.predicateConditions
				)
			}
		}

		object PredicateAsListSerializer : KSerializer<Predicate> by PredicateSerializer {
			override val descriptor = buildClassSerialDescriptor("PredicateAsList")

			override fun serialize(encoder: Encoder, value: Predicate) {
				encoder.encodeSerializableValue(
					ListSerializer(serializer<PredicateCondition>()),
					value.predicateConditions
				)
			}
		}
	}
}

fun DataPack.predicate(fileName: String, predicate: Predicate.() -> Unit): PredicateArgument {
	predicates += Predicate(fileName).apply(predicate)
	return PredicateArgument(fileName, name)
}
