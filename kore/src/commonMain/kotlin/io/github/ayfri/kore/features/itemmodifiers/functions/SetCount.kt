package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.providers.ContextIntProvider
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.features.predicates.providers.utils.toUniformProvider
import kotlinx.serialization.Serializable

/**
 * Sets the item stack count (optionally adding to the existing count). Mirrors `minecraft:set_count`.
 * Accepts a context int provider for dynamic values.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetCount(
	override var condition: PredicateCondition? = null,
	var count: ContextIntProvider,
	var add: Boolean? = null,
) : ItemFunction()

/** Add a `set_count` step with a context int provider. */
fun ItemModifier.setCount(count: ContextIntProvider, add: Boolean? = null, block: SetCount.() -> Unit = {}) {
	modifiers += SetCount(count = count, add = add).apply(block)
}

/** Int convenience overload for `set_count`. */
fun ItemModifier.setCount(count: Int, add: Boolean? = null, block: SetCount.() -> Unit = {}) {
	modifiers += SetCount(count = constant(count), add = add).apply(block)
}

/**
 * Add a `set_count` step drawing the count uniformly from [count], both ends included.
 *
 * ```
 * setCount(1..3)  // "count": {"type": "minecraft:uniform", "max": 3, "min": 1}
 * ```
 */
fun ItemModifier.setCount(count: IntRange, add: Boolean? = null, block: SetCount.() -> Unit = {}) =
	setCount(count.toUniformProvider(), add, block)
