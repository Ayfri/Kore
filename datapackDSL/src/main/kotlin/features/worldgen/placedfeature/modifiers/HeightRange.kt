package features.worldgen.placedfeature.modifiers

import features.worldgen.heightproviders.HeightProvider
import features.worldgen.heightproviders.constantAbsolute
import kotlinx.serialization.Serializable

@Serializable
data class HeightRange(
	var height: HeightProvider = constantAbsolute(0),
) : PlacementModifier()

fun heightRange(height: HeightProvider = constantAbsolute(0)) = HeightRange(height)

fun MutableList<PlacementModifier>.heightRange(height: HeightProvider = constantAbsolute(0)) {
	this += HeightRange(height)
}
