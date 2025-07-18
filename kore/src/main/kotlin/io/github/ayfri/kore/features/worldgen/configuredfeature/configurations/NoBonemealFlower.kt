package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.generated.arguments.worldgen.types.PlacedFeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data class NoBonemealFlower(
	var tries: Int? = null,
	var xzSpread: Int? = null,
	var ySpread: Int? = null,
	var placedFeature: PlacedFeatureArgument,
) : FeatureConfig()

fun noBonemealFlower(
	tries: Int? = null,
	xzSpread: Int? = null,
	ySpread: Int? = null,
	placedFeature: PlacedFeatureArgument,
) = NoBonemealFlower(tries, xzSpread, ySpread, placedFeature)

fun noBonemealFlower(
	placedFeature: PlacedFeatureArgument,
	block: NoBonemealFlower.() -> Unit = {},
) = NoBonemealFlower(placedFeature = placedFeature).apply(block)
