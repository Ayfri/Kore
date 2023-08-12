package features.worldgen.processorlist.types

import features.worldgen.processorlist.ProcessorList
import kotlinx.serialization.Serializable

@Serializable
data object LavaSubmergedBlock : ProcessorType()

fun ProcessorList.lavaSubmergedBlock() {
	processors += LavaSubmergedBlock
}
