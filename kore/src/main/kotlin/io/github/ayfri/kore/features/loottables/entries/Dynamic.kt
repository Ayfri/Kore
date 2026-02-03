package io.github.ayfri.kore.features.loottables.entries

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.itemmodifiers.ItemModifierAsList
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

/**
 * Loot entry that pulls items from a dynamic context (e.g., `minecraft:contents`).
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/loot-tables
 * Minecraft Wiki: https://minecraft.wiki/w/Loot_table
 */
@Serializable
data class Dynamic(
	var name: String,
	var conditions: PredicateAsList? = null,
	var functions: ItemModifierAsList? = null,
	var quality: Int? = null,
	var weight: Int? = null,
) : LootEntry()

/** Add and configure a Dynamic entry. */
fun LootEntries.dynamic(name: String, block: Dynamic.() -> Unit = {}) {
	add(Dynamic(name).apply(block))
}

/** Set conditions, see [Predicates](https://kore.ayfri.com/docs/data-driven/predicates). */
fun Dynamic.conditions(block: Predicate.() -> Unit) {
	conditions = Predicate().apply(block)
}

/** Set item modifier functions, see [ItemModifiers](https://kore.ayfri.com/docs/data-driven/item-modifiers). */
fun Dynamic.functions(block: ItemModifier.() -> Unit) {
	functions = ItemModifier().apply(block)
}
