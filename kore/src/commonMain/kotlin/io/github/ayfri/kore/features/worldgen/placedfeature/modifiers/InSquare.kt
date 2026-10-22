package io.github.ayfri.kore.features.worldgen.placedfeature.modifiers

import kotlinx.serialization.Serializable

@Serializable
data object InSquare : PlacementModifier()

fun PlacementModifiersScope.inSquare() {
	placementModifiers += InSquare
}
