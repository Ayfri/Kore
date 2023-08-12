package features.worldgen.processorlist.types

import features.worldgen.intproviders.IntProvider
import features.worldgen.intproviders.constant
import features.worldgen.processorlist.ProcessorList
import kotlinx.serialization.Serializable

@Serializable
data class Capped(
	var limit: IntProvider = constant(0),
	var delegate: ProcessorType = Nop,
) : ProcessorType()

fun ProcessorList.capped(limit: IntProvider = constant(0), delegate: ProcessorType = Nop) {
	processors += Capped(limit, delegate)
}

fun ProcessorList.capped(limit: Int, delegate: ProcessorType = Nop) {
	processors += Capped(constant(limit), delegate)
}
