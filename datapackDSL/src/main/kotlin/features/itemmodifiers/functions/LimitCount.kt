package features.itemmodifiers.functions

import features.itemmodifiers.ItemModifier
import features.predicates.PredicateAsList
import features.predicates.providers.IntOrIntNumberProvidersRange
import features.predicates.providers.int
import features.predicates.providers.intRange
import kotlinx.serialization.Serializable

@Serializable
data class LimitCount(
	override var conditions: PredicateAsList? = null,
	val limit: IntOrIntNumberProvidersRange,
) : ItemFunction()

fun ItemModifier.limitCount(limit: IntOrIntNumberProvidersRange, block: LimitCount.() -> Unit = {}) {
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
