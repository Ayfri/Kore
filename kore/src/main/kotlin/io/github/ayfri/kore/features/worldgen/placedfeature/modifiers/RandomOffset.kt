package io.github.ayfri.kore.features.worldgen.placedfeature.modifiers

import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.features.worldgen.placedfeature.PlacedFeature
import kotlinx.serialization.Serializable

@Serializable
data class RandomOffset(
	var xzSpread: IntProvider = constant(0),
	var ySpread: IntProvider = constant(0),
) : PlacementModifier()

fun PlacedFeature.randomOffset(xzSpread: IntProvider = constant(0), ySpread: IntProvider = constant(0)) {
	placementModifiers += RandomOffset(xzSpread, ySpread)
}
