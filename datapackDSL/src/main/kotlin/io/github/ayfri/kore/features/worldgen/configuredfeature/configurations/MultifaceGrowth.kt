package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class MultifaceGrowth(
	var block: BlockArgument? = null,
	var searchRange: Int? = null,
	var chanceOfSpreading: Double? = null,
	var canPlaceOnFloor: Boolean? = null,
	var canPlaceOnCeiling: Boolean? = null,
	var canPlaceOnWall: Boolean? = null,
	var canBePlacedOn: InlinableList<BlockOrTagArgument> = emptyList(),
) : FeatureConfig()

fun multifaceGrowth(block: MultifaceGrowth.() -> Unit = {}) = MultifaceGrowth().apply(block)
