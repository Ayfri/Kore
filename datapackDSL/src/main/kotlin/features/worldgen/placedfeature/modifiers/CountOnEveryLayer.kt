package features.worldgen.placedfeature.modifiers

import features.worldgen.intproviders.IntProvider
import features.worldgen.intproviders.constant
import kotlinx.serialization.Serializable

@Serializable
data class CountOnEveryLayer(
	var count: IntProvider = constant(0),
) : PlacementModifier()

fun countOnEveryLayer(count: IntProvider = constant(0)) = CountOnEveryLayer(count)

fun MutableList<PlacementModifier>.countOnEveryLayer(count: IntProvider = constant(0)) {
	this += CountOnEveryLayer(count)
}
