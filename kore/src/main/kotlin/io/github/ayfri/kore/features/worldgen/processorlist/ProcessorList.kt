package io.github.ayfri.kore.features.worldgen.processorlist

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.resources.worldgen.ProcessorListArgument
import io.github.ayfri.kore.features.worldgen.processorlist.types.ProcessorType
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

fun DataPack.processorList(fileName: String = "processor_list", block: ProcessorList.() -> Unit): ProcessorListArgument {
	processorLists += ProcessorList(fileName).apply(block)
	return ProcessorListArgument(fileName, name)
}
