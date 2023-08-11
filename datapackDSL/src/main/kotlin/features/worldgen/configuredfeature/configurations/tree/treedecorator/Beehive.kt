package features.worldgen.configuredfeature.configurations.tree.treedecorator

import kotlinx.serialization.Serializable

@Serializable
data class Beehive(
	var probability: Double = 0.0,
) : TreeDecorator()

fun beehive(probability: Double = 0.0) = Beehive(probability)

fun MutableList<TreeDecorator>.beehive(probability: Double = 0.0) {
	this += Beehive(probability)
}
