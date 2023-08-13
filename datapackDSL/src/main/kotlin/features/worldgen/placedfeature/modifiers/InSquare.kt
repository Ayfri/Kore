package features.worldgen.placedfeature.modifiers

import features.worldgen.placedfeature.PlacedFeature
import kotlinx.serialization.Serializable

@Serializable
data object InSquare : PlacementModifier()

fun PlacedFeature.inSquare() {
	placementModifiers += InSquare
}
