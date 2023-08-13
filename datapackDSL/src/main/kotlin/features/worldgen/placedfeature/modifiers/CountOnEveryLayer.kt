package features.worldgen.placedfeature.modifiers

import features.worldgen.intproviders.IntProvider
import features.worldgen.intproviders.constant
import features.worldgen.placedfeature.PlacedFeature
import kotlinx.serialization.Serializable

@Serializable
data class CountOnEveryLayer(
	var count: IntProvider = constant(0),
) : PlacementModifier()

fun PlacedFeature.countOnEveryLayer(count: IntProvider = constant(0)) {
	placementModifiers += CountOnEveryLayer(count)
}
