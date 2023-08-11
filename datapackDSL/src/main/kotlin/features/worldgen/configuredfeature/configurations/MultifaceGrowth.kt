package features.worldgen.configuredfeature.configurations

import arguments.types.BlockOrTagArgument
import arguments.types.resources.BlockArgument
import serializers.InlinableList
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
