package io.github.ayfri.kore.features.worldgen.placedfeature.modifiers

import io.github.ayfri.kore.features.worldgen.placedfeature.PlacedFeature
import kotlinx.serialization.Serializable

@Serializable
data object InSquare : PlacementModifier()

fun PlacedFeature.inSquare() {
	placementModifiers += InSquare
}
