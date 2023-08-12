package features.worldgen.processorlist.types

import features.worldgen.processorlist.ProcessorList
import kotlinx.serialization.Serializable

@Serializable
data object Nop : ProcessorType()

fun ProcessorList.nop() {
	processors += Nop
}
