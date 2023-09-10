package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.arguments.types.resources.worldgen.PlacedFeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data class Flower(
	var tries: Int? = null,
	var xzSpread: Int? = null,
	var ySpread: Int? = null,
	var placedFeature: PlacedFeatureArgument,
) : FeatureConfig()

fun flower(
	tries: Int? = null,
	xzSpread: Int? = null,
	ySpread: Int? = null,
	placedFeature: PlacedFeatureArgument,
) = Flower(tries, xzSpread, ySpread, placedFeature)

fun flower(
	placedFeature: PlacedFeatureArgument,
	block: Flower.() -> Unit = {},
) = Flower(placedFeature = placedFeature).apply(block)
