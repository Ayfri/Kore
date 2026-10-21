package io.github.ayfri.kore.features.loottables.entries

import io.github.ayfri.kore.features.itemmodifiers.InlinableItemModifier
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.toCondition
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable


@Serializable(with = LootEntryDynamicName.Companion.LootEntryDynamicNameSerializer::class)
enum class LootEntryDynamicName {
	CONTENTS,
	SHERDS,
	;

	companion object {
		data object LootEntryDynamicNameSerializer : LowercaseSerializer<LootEntryDynamicName>(entries, {
			"minecraft:${name.lowercase()}"
		})
	}
}

/**
 * Loot entry that pulls items from a dynamic context (e.g., `minecraft:contents`).
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/loot-tables
 * Minecraft Wiki: https://minecraft.wiki/w/Loot_table
 */
@Serializable
data class Dynamic(
	var name: LootEntryDynamicName,
	var condition: PredicateCondition? = null,
	var modifier: InlinableItemModifier? = null,
	var quality: Int? = null,
	var weight: Int? = null,
) : LootEntry()

/** Add and configure a Dynamic entry. */
fun LootEntries.dynamic(name: LootEntryDynamicName, block: Dynamic.() -> Unit = {}) {
	add(Dynamic(name).apply(block))
}

/** Set the condition, several conditions being wrapped in an `all_of`, see [Predicates](https://kore.ayfri.com/docs/data-driven/predicates). */
fun Dynamic.condition(block: Predicate.() -> Unit) {
	condition = Predicate().apply(block).toCondition()
}

/** Set the item modifier, see [ItemModifiers](https://kore.ayfri.com/docs/data-driven/item-modifiers). */
fun Dynamic.modifier(block: ItemModifier.() -> Unit) {
	modifier = ItemModifier().apply(block)
}
