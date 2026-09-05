package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator

import kotlinx.serialization.Serializable

@Serializable
data class LeaveVine(
	var probability: Double = 0.0,
) : TreeDecorator()

fun leaveVine(probability: Double = 0.0) = LeaveVine(probability)

fun MutableList<TreeDecorator>.leaveVine(probability: Double = 0.0) {
	this += LeaveVine(probability)
}
