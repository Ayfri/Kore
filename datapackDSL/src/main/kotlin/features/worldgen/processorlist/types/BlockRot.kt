package features.worldgen.processorlist.types

import arguments.types.BlockOrTagArgument
import features.worldgen.processorlist.ProcessorList
import serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class BlockRot(
	var integrity: Double = 0.0,
	var rottableBlocks: InlinableList<BlockOrTagArgument> = emptyList(),
) : ProcessorType()

fun ProcessorList.blockRot(integrity: Double = 0.0, rottableBlocks: InlinableList<BlockOrTagArgument> = emptyList()) {
	processors += BlockRot(integrity, rottableBlocks)
}

fun ProcessorList.blockRot(integrity: Double = 0.0, vararg rottableBlocks: BlockOrTagArgument) {
	processors += BlockRot(integrity, rottableBlocks.toList())
}
