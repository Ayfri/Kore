package features.worldgen.placedfeature.modifiers

import features.worldgen.intproviders.IntProvider
import features.worldgen.intproviders.constant
import kotlinx.serialization.Serializable

@Serializable
data class RandomOffset(
	var xzSpread: IntProvider = constant(0),
	var ySpread: IntProvider = constant(0),
) : PlacementModifier()

fun randomOffset(xzSpread: IntProvider = constant(0), ySpread: IntProvider = constant(0)) = RandomOffset(xzSpread, ySpread)

fun MutableList<PlacementModifier>.randomOffset(xzSpread: IntProvider = constant(0), ySpread: IntProvider = constant(0)) {
	this += RandomOffset(xzSpread, ySpread)
}
