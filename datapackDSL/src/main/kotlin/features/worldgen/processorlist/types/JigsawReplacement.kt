package features.worldgen.processorlist.types

import features.worldgen.processorlist.ProcessorList
import kotlinx.serialization.Serializable

@Serializable
data object JigsawReplacement : ProcessorType()

fun ProcessorList.jigsawReplacement() {
	processors += JigsawReplacement
}
