package features.worldgen.configuredfeature.configurations.tree.treedecorator

import kotlinx.serialization.Serializable

@Serializable
data object TrunkVine : TreeDecorator()

fun MutableList<TreeDecorator>.trunkVine() {
	this += TrunkVine
}
