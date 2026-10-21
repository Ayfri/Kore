package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.InlinableItemModifier
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import kotlinx.serialization.Serializable

/**
 * Groups a nested list of functions to run in order. Mirrors vanilla `minecraft:sequence`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class Sequence(
	override var condition: PredicateCondition? = null,
	val functions: InlinableItemModifier? = null,
) : ItemFunction()

/** Add a `sequence` step from a nested builder. */
fun ItemModifier.sequence(block: ItemModifier.() -> Unit = {}) {
	modifiers += Sequence(functions = ItemModifier().apply(block))
}
