package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import io.github.ayfri.kore.features.predicates.providers.constant
import kotlinx.serialization.Serializable

/**
 * Sets the item stack count (optionally adding to the existing count). Mirrors `minecraft:set_count`.
 * Accepts a number provider for dynamic values.
 *
 * Docs: https://kore.ayfri.com/docs/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetCount(
	override var conditions: PredicateAsList? = null,
	var count: NumberProvider,
	var add: Boolean? = null,
) : ItemFunction()

/** Add a `set_count` step with a number provider. */
fun ItemModifier.setCount(count: NumberProvider, add: Boolean? = null, block: SetCount.() -> Unit = {}) {
	modifiers += SetCount(count = count, add = add).apply(block)
}

/** Float convenience overload for `set_count`. */
fun ItemModifier.setCount(count: Float, add: Boolean? = null, block: SetCount.() -> Unit = {}) {
	modifiers += SetCount(count = constant(count), add = add).apply(block)
}
