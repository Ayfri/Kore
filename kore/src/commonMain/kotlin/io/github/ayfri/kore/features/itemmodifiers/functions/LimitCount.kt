package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.providers.ConstantContextIntProvider
import io.github.ayfri.kore.features.predicates.providers.ContextIntProvider
import io.github.ayfri.kore.features.predicates.providers.ContextIntProviderBounds
import kotlinx.serialization.Serializable

/**
 * Clamps the item stack size to a range or maximum value.
 * Mirrors vanilla `minecraft:limit_count`.
 *
 * [limit] is always a `{ "min": ..., "max": ... }` object, the game rejects a bare number.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class LimitCount(
	override var condition: PredicateCondition? = null,
	val limit: ContextIntProviderBounds,
) : ItemFunction()

/** Add a `limit_count` step with bounds. */
fun ItemModifier.limitCount(limit: ContextIntProviderBounds, block: LimitCount.() -> Unit = {}) {
	modifiers += LimitCount(limit = limit).apply(block)
}

/**
 * Add a `limit_count` step clamping the count to [range], the bounds being written as is and nothing being drawn.
 *
 * ```
 * limitCount(1..32)  // "limit": { "max": 32, "min": 1 }
 * ```
 */
fun ItemModifier.limitCount(range: IntRange, block: LimitCount.() -> Unit = {}) = limitCount(range.first, range.last, block)

/**
 * Add a `limit_count` step clamping the count between [min] and [max], both included.
 *
 * ```
 * limitCount(1, 32)  // "limit": { "max": 32, "min": 1 }
 * ```
 */
fun ItemModifier.limitCount(min: Int, max: Int, block: LimitCount.() -> Unit = {}) =
	limitCount(ConstantContextIntProvider(min), ConstantContextIntProvider(max), block)

/**
 * Add a `limit_count` step with the optional provider bounds [min] and [max], a `null` bound leaving that side open.
 *
 * ```
 * limitCount(max = constant(64))  // "limit": { "max": 64 }
 * ```
 */
fun ItemModifier.limitCount(min: ContextIntProvider? = null, max: ContextIntProvider? = null, block: LimitCount.() -> Unit = {}) {
	modifiers += LimitCount(limit = ContextIntProviderBounds(max, min)).apply(block)
}
