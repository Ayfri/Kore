package io.github.ayfri.kore.features.worldgen.placedfeature.modifiers

import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import io.github.ayfri.kore.features.worldgen.placedfeature.PlacedFeature
import kotlinx.serialization.Serializable

@Serializable
data class Count(
	var count: IntProvider = ConstantIntProvider(0),
) : PlacementModifier(), IntProviderScope

fun PlacedFeature.count(count: IntProvider = ConstantIntProvider(0)) {
	placementModifiers += Count(count)
}
