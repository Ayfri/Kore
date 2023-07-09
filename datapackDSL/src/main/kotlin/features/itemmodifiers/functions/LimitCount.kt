package features.itemmodifiers.functions

import features.itemmodifiers.ItemModifierEntry
import features.predicates.providers.IntOrIntNumberProvidersRange
import features.predicates.providers.int
import features.predicates.providers.intRange
import kotlinx.serialization.Serializable

@Serializable
data class LimitCount(
	val limit: IntOrIntNumberProvidersRange
) : ItemFunctionSurrogate

fun ItemModifierEntry.limitCount(limit: IntOrIntNumberProvidersRange) {
	function = LimitCount(limit)
}

fun ItemModifierEntry.limitCount(limit: Int) = limitCount(int(limit))

fun ItemModifierEntry.limitCount(range: ClosedFloatingPointRange<Float>) = limitCount(intRange(range))

fun ItemModifierEntry.limitCount(min: Float, max: Float) = limitCount(intRange(min, max))
