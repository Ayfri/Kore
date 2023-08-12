package features.worldgen.processorlist.types

import features.worldgen.processorlist.ProcessorList
import kotlinx.serialization.Serializable

@Serializable
data object BlackStoneReplace : ProcessorType()

fun ProcessorList.blackStoneReplace() {
	processors += BlackStoneReplace
}
