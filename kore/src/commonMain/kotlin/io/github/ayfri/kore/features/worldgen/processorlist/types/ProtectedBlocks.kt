package io.github.ayfri.kore.features.worldgen.processorlist.types

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.features.worldgen.processorlist.ProcessorList
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class ProtectedBlocks(
	var value: InlinableList<BlockOrTagArgument>,
) : ProcessorType()

fun ProcessorList.protectedBlocks(value: InlinableList<BlockOrTagArgument>) {
	processors += ProtectedBlocks(value)
}
