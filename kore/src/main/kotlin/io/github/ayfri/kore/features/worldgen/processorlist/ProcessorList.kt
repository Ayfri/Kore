package io.github.ayfri.kore.features.worldgen.processorlist

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.worldgen.processorlist.types.ProcessorType
import io.github.ayfri.kore.generated.arguments.worldgen.types.ProcessorListArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString

@Serializable
data class ProcessorList(
	@Transient
	override var fileName: String = "processor_list",
	var processors: List<ProcessorType> = emptyList(),
) : Generator("worldgen/processor_list") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

fun DataPack.processorList(fileName: String = "processor_list", init: ProcessorList.() -> Unit = {}): ProcessorListArgument {
	val processorList = ProcessorList(fileName).apply(init)
	processorLists += processorList
	return ProcessorListArgument(fileName, processorList.namespace ?: name)
}
