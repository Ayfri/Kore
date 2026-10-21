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
	val functions: InlinableItemModifier,
) : ItemFunction()

/**
 * Add a `sequence` step from a nested builder and return it to guard it with a [condition].
 *
 * ```
 * sequence { setItem(Items.APPLE) }.condition { randomChance(0.5f) }
 * ```
 */
fun ItemModifier.sequence(block: ItemModifier.() -> Unit = {}) =
	Sequence(functions = ItemModifier().apply(block)).also { modifiers += it }
