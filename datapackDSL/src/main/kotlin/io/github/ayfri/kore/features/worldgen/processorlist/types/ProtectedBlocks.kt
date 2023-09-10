package io.github.ayfri.kore.features.worldgen.processorlist.types

import io.github.ayfri.kore.arguments.types.resources.tagged.BlockTagArgument
import io.github.ayfri.kore.features.worldgen.processorlist.ProcessorList
import kotlinx.serialization.Serializable

@Serializable
data class ProtectedBlocks(
	var value: BlockTagArgument,
) : ProcessorType()

fun ProcessorList.protectedBlocks(value: BlockTagArgument) {
	processors += ProtectedBlocks(value)
}
