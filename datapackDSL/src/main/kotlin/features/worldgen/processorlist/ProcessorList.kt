package features.worldgen.processorlist

import DataPack
import Generator
import arguments.types.resources.worldgen.ProcessorArgument
import features.worldgen.processorlist.types.ProcessorType
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString

@Serializable
data class ProcessorList(
	@Transient
	override var fileName: String = "processor_list",
	var processors: List<ProcessorType> = emptyList(),
) : Generator {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

fun DataPack.processorList(fileName: String = "processor_list", block: ProcessorList.() -> Unit): ProcessorArgument {
	processorLists += ProcessorList().apply(block)
	return ProcessorArgument(fileName, name)
}
