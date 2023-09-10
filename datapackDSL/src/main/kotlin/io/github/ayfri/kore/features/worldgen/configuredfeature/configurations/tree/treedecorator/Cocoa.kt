package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator

import kotlinx.serialization.Serializable

@Serializable
data class Cocoa(
	var probability: Double = 0.0,
) : TreeDecorator()

fun cocoa(probability: Double = 0.0) = Cocoa(probability)

fun MutableList<TreeDecorator>.cocoa(probability: Double = 0.0) {
	this += Cocoa(probability)
}
