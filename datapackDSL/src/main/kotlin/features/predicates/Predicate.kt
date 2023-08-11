package features.predicates

import DataPack
import Generator
import arguments.types.resources.PredicateArgument
import features.predicates.conditions.PredicateCondition
import features.predicates.conditions.PredicateConditionSurrogate
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

typealias PredicateAsList = @Serializable(Predicate.Companion.PredicateAsListSerializer::class) Predicate

@Serializable(with = Predicate.Companion.PredicateSerializer::class)
data class Predicate(
	override var fileName: String = "predicate",
	var predicateConditions: List<PredicateCondition> = mutableListOf(),
) : Generator {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)

	companion object {
		object PredicateSerializer : KSerializer<Predicate> {
			override val descriptor = buildClassSerialDescriptor("Predicate")
			override fun deserialize(decoder: Decoder) = error("Predicate cannot be deserialized")

			override fun serialize(encoder: Encoder, value: Predicate) {
				val serializer = PredicateConditionSurrogate.Companion.PredicateConditionSerializer
				when (value.predicateConditions.size) {
					1 -> encoder.encodeSerializableValue(serializer, value.predicateConditions[0])
					else -> encoder.encodeSerializableValue(ListSerializer(serializer), value.predicateConditions)
				}
			}
		}

		object PredicateAsListSerializer : KSerializer<Predicate> by PredicateSerializer {
			override val descriptor = buildClassSerialDescriptor("PredicateAsList")
			override fun serialize(encoder: Encoder, value: Predicate) {
				encoder.encodeSerializableValue(
					ListSerializer(PredicateConditionSurrogate.Companion.PredicateConditionSerializer),
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
