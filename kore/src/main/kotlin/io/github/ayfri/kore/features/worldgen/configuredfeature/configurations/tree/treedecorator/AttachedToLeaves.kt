package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator

import io.github.ayfri.kore.features.worldgen.configuredfeature.Direction
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttachedToLeaves(
	var probability: Double = 0.0,
	@SerialName("exclusion_radius_xz")
	var exclusionRadiusXZ: Int = 0,
	var exclusionRadiusY: Int = 0,
	var requiredEmptyBlocks: Int = 0,
	var blockProvider: BlockStateProvider = simpleStateProvider(),
	var directions: List<Direction> = emptyList(),
) : TreeDecorator()

fun attachedToLeaves(block: AttachedToLeaves.() -> Unit = {}) = AttachedToLeaves().apply(block)

fun MutableList<TreeDecorator>.attachedToLeaves(block: AttachedToLeaves.() -> Unit = {}) {
	this += AttachedToLeaves().apply(block)
}
