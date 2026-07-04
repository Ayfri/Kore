package io.github.ayfri.kore.features.worldgen.processorlist.types

import io.github.ayfri.kore.features.worldgen.processorlist.ProcessorList
import kotlinx.serialization.Serializable

@Serializable
data object Nop : ProcessorType()

fun ProcessorList.nop() {
	processors += Nop
}
