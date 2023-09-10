package io.github.ayfri.kore.features.loottables.entries

import io.github.ayfri.kore.arguments.types.resources.LootTableArgument
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.itemmodifiers.ItemModifierAsList
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class LootTable(
	var name: LootTableArgument,
	var conditions: PredicateAsList? = null,
	var functions: ItemModifierAsList? = null,
	var quality: Int? = null,
	var weight: Int? = null,
) : LootEntry()

fun LootEntries.lootTable(name: LootTableArgument, block: LootTable.() -> Unit = {}) {
	add(LootTable(name).apply(block))
}

fun LootTable.conditions(block: PredicateAsList.() -> Unit) {
	conditions = PredicateAsList().apply(block)
}

fun LootTable.functions(block: ItemModifier.() -> Unit) {
	functions = ItemModifier().apply(block)
}
