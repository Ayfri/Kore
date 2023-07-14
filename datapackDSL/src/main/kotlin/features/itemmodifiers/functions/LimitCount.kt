package features.itemmodifiers.functions

import features.itemmodifiers.ItemModifier
import features.predicates.providers.IntOrIntNumberProvidersRange
import features.predicates.providers.int
import features.predicates.providers.intRange
import kotlinx.serialization.Serializable

@Serializable
data class LimitCount(
	val limit: IntOrIntNumberProvidersRange
) : ItemFunction()

fun ItemModifier.limitCount(limit: IntOrIntNumberProvidersRange, block: LimitCount.() -> Unit = {}) {
	modifiers += LimitCount(limit).apply(block)
}

fun ItemModifier.limitCount(limit: Int, block: LimitCount.() -> Unit = {}) {
	modifiers += LimitCount(int(limit)).apply(block)
}

fun ItemModifier.limitCount(range: ClosedFloatingPointRange<Float>, block: LimitCount.() -> Unit = {}) {
	modifiers += LimitCount(intRange(range)).apply(block)
}

fun ItemModifier.limitCount(min: Float, max: Float, block: LimitCount.() -> Unit = {}) {
	modifiers += LimitCount(intRange(min, max)).apply(block)
}
