package features.worldgen.processorlist.types

import arguments.types.resources.tagged.BlockTagArgument
import features.worldgen.processorlist.ProcessorList
import kotlinx.serialization.Serializable

@Serializable
data class ProtectedBlocks(
	var value: BlockTagArgument,
) : ProcessorType()

fun ProcessorList.protectedBlocks(value: BlockTagArgument) {
	processors += ProtectedBlocks(value)
}
