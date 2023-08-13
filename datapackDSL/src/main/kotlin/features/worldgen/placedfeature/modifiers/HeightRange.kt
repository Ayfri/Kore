package features.worldgen.placedfeature.modifiers

import features.worldgen.heightproviders.HeightProvider
import features.worldgen.heightproviders.constantAbsolute
import features.worldgen.placedfeature.PlacedFeature
import kotlinx.serialization.Serializable

@Serializable
data class HeightRange(
	var height: HeightProvider = constantAbsolute(0),
) : PlacementModifier()

fun PlacedFeature.heightRange(height: HeightProvider = constantAbsolute(0)) {
	placementModifiers += HeightRange(height)
}
