package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.layersfeaturesize

import kotlinx.serialization.Serializable

@Serializable
data class ThreeLayersFeatureSize(
	override var minClippedHeight: Int? = null,
	var limit: Int? = null,
	var upperLimit: Int? = null,
	var lowerSize: Int? = null,
	var middleSize: Int? = null,
	var upperSize: Int? = null,
) : LayersFeatureSize()

fun threeLayersFeatureSize(block: ThreeLayersFeatureSize.() -> Unit = {}) = ThreeLayersFeatureSize().apply(block)
