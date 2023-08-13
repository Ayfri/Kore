package features.worldgen.placedfeature.modifiers

import features.worldgen.intproviders.IntProvider
import features.worldgen.intproviders.constant
import features.worldgen.placedfeature.PlacedFeature
import kotlinx.serialization.Serializable

@Serializable
data class RandomOffset(
	var xzSpread: IntProvider = constant(0),
	var ySpread: IntProvider = constant(0),
) : PlacementModifier()

fun PlacedFeature.randomOffset(xzSpread: IntProvider = constant(0), ySpread: IntProvider = constant(0)) {
	placementModifiers += RandomOffset(xzSpread, ySpread)
}
