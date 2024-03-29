package io.github.ayfri.kore.features.loottables

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.itemmodifiers.ItemModifierAsList
import io.github.ayfri.kore.features.loottables.entries.LootEntries
import io.github.ayfri.kore.features.loottables.entries.LootEntry
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import kotlinx.serialization.Serializable

@Serializable
data class LootPool(
	var rolls: NumberProvider,
	var bonusRolls: NumberProvider? = null,
	var conditions: PredicateAsList? = null,
	var entries: List<LootEntry> = emptyList(),
	var functions: ItemModifierAsList? = null,
)

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
