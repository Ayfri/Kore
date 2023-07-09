package features.loottables

import features.itemmodifiers.ItemModifier
import features.itemmodifiers.ItemModifierEntry
import features.loottables.entries.LootEntries
import features.loottables.entries.LootEntry
import features.loottables.entries.LootEntrySurrogate
import features.predicates.Predicate
import features.predicates.conditions.PredicateCondition
import features.predicates.conditions.PredicateConditionsSerializer
import features.predicates.providers.NumberProvider
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

typealias LootPool = @Serializable(LootPoolSerializer::class) LootPoolSurrogate

@Serializable
data class LootPoolSurrogate(
	var rolls: NumberProvider,
	var bonusRolls: NumberProvider? = null,
	@Serializable(with = PredicateConditionsSerializer::class)
	var conditions: List<PredicateCondition>? = null,
	var entries: List<LootEntry> = emptyList(),
	var functions: ItemModifier? = null,
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

			put("entries", Json.encodeToJsonElement(ListSerializer(LootEntrySurrogate.Companion.LootEntrySerializer), value.entries))

			value.functions?.let {
				put("functions", Json.encodeToJsonElement(ItemModifier.serializer(), it))
			}
		}

		encoder.encodeJsonElement(result)
	}
}

fun LootPoolSurrogate.rolls(value: NumberProvider) {
	rolls = value
}

fun LootPoolSurrogate.bonusRolls(value: NumberProvider) {
	bonusRolls = value
}

fun LootPoolSurrogate.conditions(vararg value: PredicateCondition) {
	conditions = value.toList()
}

fun LootPoolSurrogate.conditions(block: Predicate.() -> Unit) {
	conditions = Predicate().apply(block).predicateConditions
}

fun LootPoolSurrogate.entries(vararg value: LootEntry) {
	entries = value.toList()
}

fun LootPoolSurrogate.entries(block: LootEntries.() -> Unit) {
	entries = buildList(block)
}

fun LootPoolSurrogate.functions(block: ItemModifierEntry.() -> Unit) {
	functions = ItemModifier().apply {
		modifiers += ItemModifierEntry().apply(block)
	}
}
