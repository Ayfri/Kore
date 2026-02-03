package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.features.predicates.providers.IntOrNumberProvidersRange
import io.github.ayfri.kore.features.predicates.providers.int
import io.github.ayfri.kore.features.predicates.providers.intRange
import kotlinx.serialization.Serializable

/**
 * Clamps the item stack size to a range or maximum value.
 * Mirrors vanilla `minecraft:limit_count`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class LimitCount(
	override var conditions: PredicateAsList? = null,
	val limit: IntOrNumberProvidersRange,
) : ItemFunction()

/** Add a `limit_count` step with a range/provider. */
fun ItemModifier.limitCount(limit: IntOrNumberProvidersRange, block: LimitCount.() -> Unit = {}) {
	modifiers += LimitCount(limit = limit).apply(block)
}

/** Convenience overload with a fixed maximum. */
fun ItemModifier.limitCount(limit: Int, block: LimitCount.() -> Unit = {}) {
	modifiers += LimitCount(limit = int(limit)).apply(block)
}

/** Convenience overload with a float range. */
fun ItemModifier.limitCount(range: ClosedFloatingPointRange<Float>, block: LimitCount.() -> Unit = {}) {
	modifiers += LimitCount(limit = intRange(range)).apply(block)
}

/** Convenience overload with min/max. */
fun ItemModifier.limitCount(min: Float, max: Float, block: LimitCount.() -> Unit = {}) {
	modifiers += LimitCount(limit = intRange(min, max)).apply(block)
}
