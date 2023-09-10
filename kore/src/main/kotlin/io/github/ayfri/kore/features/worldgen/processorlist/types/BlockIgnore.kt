package io.github.ayfri.kore.features.worldgen.processorlist.types

import io.github.ayfri.kore.data.block.BlockState
import kotlinx.serialization.Serializable

@Serializable
data class BlockIgnore(
	var blocks: List<BlockState> = emptyList(),
) : ProcessorType()
