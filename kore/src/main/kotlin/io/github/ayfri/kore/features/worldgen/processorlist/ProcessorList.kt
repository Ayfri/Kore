package io.github.ayfri.kore.features.worldgen.processorlist

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.worldgen.processorlist.types.ProcessorType
import io.github.ayfri.kore.generated.arguments.worldgen.types.ProcessorListArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString

/**
 * Data-driven structure processor list.
 *
 * A sequence of processors applied when placing structures (e.g. block replacement, rules,
 * gravity, jigsaw integrity). Referenced by structure pieces and template pools.
 *
 * JSON format reference: https://minecraft.wiki/w/Processor_list
 */
@Serializable
data class ProcessorList(
	@Transient
	override var fileName: String = "processor_list",
	var processors: List<ProcessorType> = emptyList(),
) : Generator("worldgen/processor_list") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Creates a processor list with a builder block.
 *
 * Produces `data/<namespace>/worldgen/processor_list/<fileName>.json`.
 *
 * JSON format reference: https://minecraft.wiki/w/Processor_list
 * Docs: https://kore.ayfri.com/docs/worldgen
 */
fun DataPack.processorList(fileName: String = "processor_list", init: ProcessorList.() -> Unit = {}): ProcessorListArgument {
	val processorList = ProcessorList(fileName).apply(init)
	processorLists += processorList
	return ProcessorListArgument(fileName, processorList.namespace ?: name)
}
