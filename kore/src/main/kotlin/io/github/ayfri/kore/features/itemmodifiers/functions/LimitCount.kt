package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.features.predicates.providers.IntOrNumberProvidersRange
import io.github.ayfri.kore.features.predicates.providers.int
import io.github.ayfri.kore.features.predicates.providers.intRange
import kotlinx.serialization.Serializable

@Serializable
data class LimitCount(
	override var conditions: PredicateAsList? = null,
	val limit: IntOrNumberProvidersRange,
) : ItemFunction()

fun ItemModifier.limitCount(limit: IntOrNumberProvidersRange, block: LimitCount.() -> Unit = {}) {
	modifiers += LimitCount(limit = limit).apply(block)
}

fun ItemModifier.limitCount(limit: Int, block: LimitCount.() -> Unit = {}) {
	modifiers += LimitCount(limit = int(limit)).apply(block)
}

fun ItemModifier.limitCount(range: ClosedFloatingPointRange<Float>, block: LimitCount.() -> Unit = {}) {
	modifiers += LimitCount(limit = intRange(range)).apply(block)
}

fun ItemModifier.limitCount(min: Float, max: Float, block: LimitCount.() -> Unit = {}) {
	modifiers += LimitCount(limit = intRange(min, max)).apply(block)
}
