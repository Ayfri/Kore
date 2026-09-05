package io.github.ayfri.kore.features.worldgen.processorlist

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.worldgen.processorlist.types.ProcessorType
import io.github.ayfri.kore.features.worldgen.processorlist.types.ProcessorsScope
import io.github.ayfri.kore.generated.arguments.worldgen.types.ProcessorListArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * A structure processor list, a sequence of processors applied to every block of a structure template when it is
 * placed (block replacement, aging, gravity, jigsaw integrity...).
 *
 * Processors run in declaration order, each one taking the output of the previous one as its input. Referenced by
 * template pool elements and by features like `fossil`.
 *
 * Produces `data/<namespace>/worldgen/processor_list/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 *
 * @property processors The processors to apply, in application order.
 */
@Serializable
data class ProcessorList(
	@Transient
	override var fileName: String = "processor_list",
	override val processors: MutableList<ProcessorType> = mutableListOf(),
) : Generator("worldgen/processor_list"), ProcessorsScope {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Creates a processor list, appending the processors declared in [init].
 *
 * ```kotlin
 * val aging = processorList("aging") {
 *     blockAge(0.5)
 *     gravity(HeightMap.WORLD_SURFACE_WG)
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/processor_list/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
fun DataPack.processorList(fileName: String = "processor_list", init: ProcessorList.() -> Unit = {}): ProcessorListArgument {
	val processorList = ProcessorList(fileName).apply(init)
	processorLists += processorList
	return ProcessorListArgument(fileName, processorList.namespace ?: name)
}
