package io.github.ayfri.kore.features.worldgen.placedfeature.modifiers

import io.github.ayfri.kore.features.worldgen.placedfeature.PlacedFeature
import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.Serializable

@Serializable
data class FixedPlacement(
	var positions: List<TripleAsArray<Int, Int, Int>> = emptyList(),
) : PlacementModifier()

fun PlacedFeature.fixedPlacement(vararg positions: Triple<Int, Int, Int>) {
	placementModifiers += FixedPlacement(positions.map { TripleAsArray(it.first, it.second, it.third) })
}

fun PlacedFeature.fixedPlacement(init: FixedPlacement.() -> Unit) {
	placementModifiers += FixedPlacement().apply(init)
}

fun FixedPlacement.position(x: Int, y: Int, z: Int) {
	positions += TripleAsArray(x, y, z)
}
