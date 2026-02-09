package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

/**
 * Replaces the item with an empty one.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class Discard(
	override var conditions: PredicateAsList? = null,
) : ItemFunction()

/** Replaces the item with an empty one. */
fun ItemModifier.discard(block: Discard.() -> Unit = {}) =
	Discard().apply(block).also { modifiers += it }
