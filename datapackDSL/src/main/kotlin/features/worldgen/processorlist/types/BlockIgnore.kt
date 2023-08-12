package features.worldgen.processorlist.types

import data.block.BlockState
import kotlinx.serialization.Serializable

@Serializable
data class BlockIgnore(
	var blocks: List<BlockState> = emptyList(),
) : ProcessorType()
