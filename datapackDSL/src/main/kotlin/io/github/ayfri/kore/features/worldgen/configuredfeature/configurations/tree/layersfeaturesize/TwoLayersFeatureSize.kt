package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.layersfeaturesize

import kotlinx.serialization.Serializable

@Serializable
data class TwoLayersFeatureSize(
	override var minClippedHeight: Int? = null,
	var limit: Int? = null,
	var lowerSize: Int? = null,
	var upperSize: Int? = null,
) : LayersFeatureSize()

fun twoLayersFeatureSize(
	minClippedHeight: Int? = null,
	limit: Int? = null,
	lowerSize: Int? = null,
	upperSize: Int? = null,
	block: TwoLayersFeatureSize.() -> Unit = {},
) = TwoLayersFeatureSize(minClippedHeight, limit, lowerSize, upperSize).apply(block)
