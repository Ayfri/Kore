package io.github.ayfri.kore.features.worldgen.placedfeature.modifiers

import io.github.ayfri.kore.features.worldgen.heightproviders.HeightProvider
import io.github.ayfri.kore.features.worldgen.heightproviders.constantAbsolute
import io.github.ayfri.kore.features.worldgen.placedfeature.PlacedFeature
import kotlinx.serialization.Serializable

@Serializable
data class HeightRange(
	var height: HeightProvider = constantAbsolute(0),
) : PlacementModifier()

fun PlacedFeature.heightRange(height: HeightProvider = constantAbsolute(0)) {
	placementModifiers += HeightRange(height)
}
