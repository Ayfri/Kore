package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

/**
 * Copies block state properties from the loot context into the item's `block_state` component.
 * Mirrors vanilla `minecraft:copy_state`.
 *
 * Docs: https://kore.ayfri.com/docs/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class CopyState(
	override var conditions: PredicateAsList? = null,
	var block: BlockArgument,
	var properties: List<String> = emptyList(),
) : ItemFunction()

/** Add a `copy_state` step with a list of property names. */
fun ItemModifier.copyState(block: BlockArgument, properties: List<String> = emptyList(), init: CopyState.() -> Unit = {}) {
	modifiers += CopyState(block = block, properties = properties).apply(init)
}

/** Vararg convenience for `copy_state` properties. */
fun ItemModifier.copyState(block: BlockArgument, vararg properties: String, init: CopyState.() -> Unit = {}) {
	modifiers += CopyState(block = block, properties = properties.toList())
		.apply(init)
}
