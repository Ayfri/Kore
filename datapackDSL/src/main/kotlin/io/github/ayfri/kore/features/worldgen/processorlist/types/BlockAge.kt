package io.github.ayfri.kore.features.worldgen.processorlist.types

import io.github.ayfri.kore.features.worldgen.processorlist.ProcessorList
import kotlinx.serialization.Serializable

@Serializable
data class BlockAge(
	var mossiness: Double = 0.0,
) : ProcessorType()

fun ProcessorList.blockAge(mossiness: Double = 0.0) {
	processors += BlockAge(mossiness)
}
