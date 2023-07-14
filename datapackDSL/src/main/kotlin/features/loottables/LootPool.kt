package features.loottables

import dataPack
import features.itemmodifiers.ItemModifier
import features.itemmodifiers.ItemModifierAsList
import features.loottables.entries.LootEntries
import features.loottables.entries.LootEntry
import features.loottables.entries.LootEntrySurrogate
import features.predicates.Predicate
import features.predicates.PredicateAsList
import features.predicates.conditions.PredicateCondition
import features.predicates.providers.NumberProvider
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.buildJsonObject

@Serializable(with = LootPool.Companion.LootPoolSerializer::class)
data class LootPool(
	var rolls: NumberProvider,
	var bonusRolls: NumberProvider? = null,
	var conditions: PredicateAsList? = null,
	var entries: List<LootEntry> = emptyList(),
	var functions: ItemModifierAsList? = null,
) {
	companion object {
		object LootPoolSerializer : KSerializer<LootPool> {
			override val descriptor = buildClassSerialDescriptor("LootPool") {
				element<NumberProvider>("rolls")
				element<NumberProvider?>("bonus_rolls", isOptional = true)
				element<PredicateAsList>("conditions", isOptional = true)
				element<List<LootEntrySurrogate>>("entries")
				element<ItemModifierAsList?>("functions", isOptional = true)
			}

			override fun deserialize(decoder: Decoder) = error("LootPool cannot be deserialized")

			override fun serialize(encoder: Encoder, value: LootPool) {
				require(encoder is JsonEncoder) { "LootPool can only be serialized with Json" }
				val result = buildJsonObject {
					put("rolls", encoder.json.encodeToJsonElement(NumberProvider.serializer(), value.rolls))

					value.bonusRolls?.let {
						put("bonus_rolls", encoder.json.encodeToJsonElement(NumberProvider.serializer(), it))
					}

					value.conditions?.let {
						val jsonElement = it
							.getJsonEncoder(dataPack("") {})
							.encodeToJsonElement(Predicate.Companion.PredicateAsListSerializer, it)
						put("conditions", jsonElement)
					}

					put(
						"entries",
						encoder.json.encodeToJsonElement(ListSerializer(LootEntrySurrogate.Companion.LootEntrySerializer), value.entries)
					)

					value.functions?.let {
						put("functions", encoder.json.encodeToJsonElement(ItemModifier.Companion.ItemModifierAsListSerializer, it))
					}
				}

				encoder.encodeJsonElement(result)
			}
		}
	}
}

fun LootPool.rolls(value: NumberProvider) {
	rolls = value
}

fun LootPool.bonusRolls(value: NumberProvider) {
	bonusRolls = value
}

fun LootPool.conditions(vararg value: PredicateCondition) {
	conditions = conditions?.also {
		it.predicateConditions += value.toList()
	} ?: Predicate(predicateConditions = value.toList())
}

fun LootPool.conditions(block: Predicate.() -> Unit) {
	conditions = Predicate().apply(block)
}

fun LootPool.entries(vararg value: LootEntry) {
	entries = value.toList()
}

fun LootPool.entries(block: LootEntries.() -> Unit) {
	entries = buildList(block)
}

fun LootPool.functions(block: ItemModifier.() -> Unit) {
	functions = ItemModifierAsList().apply(block)
}
