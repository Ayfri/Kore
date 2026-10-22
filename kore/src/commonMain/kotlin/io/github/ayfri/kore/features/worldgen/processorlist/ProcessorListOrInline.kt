package io.github.ayfri.kore.features.worldgen.processorlist

import io.github.ayfri.kore.features.worldgen.processorlist.types.ProcessorType
import io.github.ayfri.kore.features.worldgen.processorlist.types.ProcessorsScope
import io.github.ayfri.kore.features.worldgen.processorlist.types.buildProcessors
import io.github.ayfri.kore.generated.arguments.worldgen.types.ProcessorListArgument
import io.github.ayfri.kore.serializers.EitherInlineSerializer
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable

/**
 * This class represents a processor list file ID or a processor list written inline.
 * Needed because the template pool elements and the `template` and `fossil` features accept both.
 *
 * An inline list is written as `{"processors": [...]}`, and read from that object or from the bare array.
 *
 * @property id The processor list file ID. It can be null if the instance holds inline processors.
 * @property processors The inline processors. It can be null if the instance holds a processor list file ID.
 */
@KeepGeneratedSerializer
@Serializable(with = ProcessorListOrInline.Companion.ProcessorListOrInlineSerializer::class)
data class ProcessorListOrInline(
	var id: ProcessorListArgument? = null,
	var processors: List<ProcessorType>? = null,
) {
	companion object {
		data object ProcessorListOrInlineSerializer :
			EitherInlineSerializer<ProcessorListOrInline>(generatedSerializer(), "id", decodeOnly = listOf("processors"))
	}
}

/**
 * References the processor list file [id].
 *
 * ```
 * processorListOrInline(ProcessorLists.EMPTY)  // "minecraft:empty"
 * ```
 */
fun processorListOrInline(id: ProcessorListArgument) = ProcessorListOrInline(id = id)

/**
 * Writes the processors declared in [block] inline.
 *
 * ```
 * processorListOrInline { gravity(HeightMap.WORLD_SURFACE_WG) }  // {"processors": [{"processor_type": "minecraft:gravity", ...}]}
 * ```
 */
fun processorListOrInline(block: ProcessorsScope.() -> Unit) = ProcessorListOrInline(processors = buildProcessors(block))
