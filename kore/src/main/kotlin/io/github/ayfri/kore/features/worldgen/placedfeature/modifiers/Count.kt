package io.github.ayfri.kore.features.worldgen.placedfeature.modifiers

import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.features.worldgen.placedfeature.PlacedFeature
import kotlinx.serialization.Serializable

@Serializable
data class Count(
	var count: IntProvider = constant(0),
) : PlacementModifier()

fun PlacedFeature.count(count: IntProvider = constant(0)) {
	placementModifiers += Count(count)
}
