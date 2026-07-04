package io.github.ayfri.kore.features.worldgen.processorlist.types

import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.features.worldgen.processorlist.ProcessorList
import kotlinx.serialization.Serializable

@Serializable
data class Capped(
	var limit: IntProvider = constant(0),
	var delegate: ProcessorType = Nop,
) : ProcessorType()

fun ProcessorList.capped(
	limit: IntProvider = constant(0),
	delegate: ProcessorType = Nop,
) {
	processors += Capped(limit, delegate)
}

fun ProcessorList.capped(limit: Int, delegate: ProcessorType = Nop) {
	processors += Capped(constant(limit), delegate)
}
