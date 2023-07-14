package features.predicates

import DataPack
import Generator
import arguments.Argument
import arguments.selector.Advancements
import features.advancements.types.AdvancementsJSONSerializer
import features.predicates.conditions.PredicateCondition
import features.predicates.conditions.PredicateConditionSurrogate
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

typealias PredicateAsList = @Serializable(Predicate.Companion.PredicateAsListSerializer::class) Predicate

@Serializable(with = Predicate.Companion.PredicateSerializer::class)
data class Predicate(
	override var fileName: String = "predicate",
	var predicateConditions: List<PredicateCondition> = mutableListOf(),
) : Generator {
	@Transient
	private lateinit var json: Json

	override fun generateJson(dataPack: DataPack) = getJsonEncoder(dataPack).encodeToString(this)

	@OptIn(ExperimentalSerializationApi::class)
	fun getJsonEncoder(dataPack: DataPack) = when {
		::json.isInitialized -> json
		else -> {
			json = Json {
				prettyPrint = dataPack.jsonEncoder.configuration.prettyPrint
				if (prettyPrint) prettyPrintIndent = dataPack.jsonEncoder.configuration.prettyPrintIndent
				serializersModule = SerializersModule {
					contextual(Advancements::class, AdvancementsJSONSerializer)
				}
			}
			json
		}
	}

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

fun DataPack.predicate(fileName: String, predicate: Predicate.() -> Unit): Argument.Predicate {
	predicates += Predicate(fileName).apply(predicate)
	return Argument.Predicate(fileName, name)
}
