package features.loottables

import features.loottables.entries.LootEntry
import features.predicates.conditions.PredicateCondition
import features.predicates.conditions.PredicateConditionSurrogate
import features.predicates.conditions.PredicateConditionsSerializer
import features.predicates.providers.NumberProvider
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.JsonEncoder

typealias LootPool = @Serializable(LootPoolSerializer::class) LootPoolSurrogate

@Serializable
data class LootPoolSurrogate(
	var rolls: NumberProvider,
	var bonusRolls: NumberProvider? = null,
	@Serializable(with = PredicateConditionsSerializer::class)
	var conditions: List<PredicateCondition>? = null,
	var entries: List<LootEntry>? = null,
	var functions: List<String>? = null,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LootPoolSurrogate::class)
object DefaultLootPoolSerializer

object LootPoolSerializer : KSerializer<LootPool> by DefaultLootPoolSerializer {
	override fun serialize(encoder: Encoder, value: LootPool) {
		require(encoder is JsonEncoder) { "LootPool can only be serialized with Json" }
		/*
				val result = buildJsonObject {
					put("rolls", Json.encodeToJsonElement(NumberProvider.serializer(), value.rolls))

					value.bonusRolls?.let {
						put("bonus_rolls", Json.encodeToJsonElement(NumberProvider.serializer(), it))
					}
					value.conditions?.let {
						put("conditions", Json.encodeToJsonElement(ListSerializer(PredicateCondition.serializer()), it))
					}
					value.entries?.let {
						put("entries", Json.encodeToJsonElement(ListSerializer(LootEntry.serializer()), it))
					}
					value.functions?.let {
						put("functions", Json.encodeToJsonElement(ListSerializer(String.serializer()), it))
					}
				}

				println(result)

				encoder.encodeJsonElement(result)*/
		encoder.encodeStructure(DefaultLootPoolSerializer.descriptor) {
			encodeSerializableElement(DefaultLootPoolSerializer.descriptor, 0, NumberProvider.serializer(), value.rolls)

			value.bonusRolls?.let {
				encodeSerializableElement(DefaultLootPoolSerializer.descriptor, 1, NumberProvider.serializer(), it)
			}
			value.conditions?.let {
				encodeSerializableElement(
					DefaultLootPoolSerializer.descriptor,
					2,
					ListSerializer(PredicateConditionSurrogate.Companion.PredicateConditionSerializer),
					it
				)
			}
			value.entries?.let {
				encodeSerializableElement(
					DefaultLootPoolSerializer.descriptor,
					3,
					ListSerializer(LootEntry.serializer()),
					it
				)
			}
			value.functions?.let {
				encodeSerializableElement(
					DefaultLootPoolSerializer.descriptor,
					4,
					ListSerializer(String.serializer()),
					it
				)
			}
		}
	}
}

/*
@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LootPool::class)
object DefaultLootPoolSerializer *//*
object LootPoolSerializer : JsonTransformingSerializer<LootPool>(DefaultLootPoolSerializer) {
	override fun transformSerialize(element: JsonElement): JsonElement {
		println(element)
		 *//* element.jsonObject["conditions"]?.jsonArray?.let { conditions ->
			if (conditions[0].jsonPrimitive.contentOrNull != "kotlin.collections.ArrayList") return@transformSerialize element

			return@transformSerialize buildJsonObject {
				put("conditions", JsonArray(conditions.drop(1)))

				element.jsonObject.forEach { (key, value) ->
					if (key != "conditions") {
						put(key, value)
					}
				}
			}
		} *//*

		return element
	}
} */
