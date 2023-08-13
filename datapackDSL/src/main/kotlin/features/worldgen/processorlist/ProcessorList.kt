package features.worldgen.processorlist

import DataPack
import Generator
import arguments.types.resources.worldgen.ProcessorListArgument
import features.worldgen.processorlist.types.ProcessorType
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
	processorLists += ProcessorList().apply(block)
	return ProcessorListArgument(fileName, name)
}
