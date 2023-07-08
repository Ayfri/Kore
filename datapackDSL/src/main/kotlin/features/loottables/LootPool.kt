package features.loottables

import features.loottables.entries.LootEntry
import features.loottables.entries.LootEntrySurrogate
import features.predicates.conditions.PredicateCondition
import features.predicates.conditions.PredicateConditionsSerializer
import features.predicates.providers.NumberProvider
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

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
	private val WORKAROUND_FIELD = "______do_not_use_this_field______${hashCode()}"
	private val workaroundJson = Json {
		classDiscriminator = WORKAROUND_FIELD
	}

	override fun serialize(encoder: Encoder, value: LootPool) {
		require(encoder is JsonEncoder) { "LootPool can only be serialized with Json" }
		val result = buildJsonObject {
			put("rolls", Json.encodeToJsonElement(NumberProvider.serializer(), value.rolls))

			value.bonusRolls?.let {
				put("bonus_rolls", Json.encodeToJsonElement(NumberProvider.serializer(), it))
			}

			value.conditions?.let {
				val element = workaroundJson.encodeToJsonElement(ListSerializer(PredicateCondition.serializer()), it)
				val resultElement = element.jsonArray.map { jsonElement ->
					JsonObject(jsonElement.jsonObject.filterKeys { key -> key != WORKAROUND_FIELD })
				}

				put("conditions", JsonArray(resultElement))
			}

			value.entries?.let {
				put("entries", Json.encodeToJsonElement(ListSerializer(LootEntrySurrogate.Companion.LootEntrySerializer), it))
			}

			value.functions?.let {
				put("functions", Json.encodeToJsonElement(ListSerializer(String.serializer()), it))
			}
		}

		encoder.encodeJsonElement(result)
	}
}
