package io.github.ayfri.kore.features.worldgen.templatepool.elements

import io.github.ayfri.kore.features.worldgen.processorlist.ProcessorListOrInline
import io.github.ayfri.kore.features.worldgen.processorlist.processorListOrInline
import io.github.ayfri.kore.features.worldgen.structures.types.jigsaw.LiquidSettings
import io.github.ayfri.kore.features.worldgen.templatepool.Projection
import io.github.ayfri.kore.features.worldgen.templatepool.TemplatePoolEntry
import io.github.ayfri.kore.generated.ProcessorLists
import io.github.ayfri.kore.generated.arguments.worldgen.types.ProcessorListArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.StructureArgument
import kotlinx.serialization.Serializable

/**
 * Places a structure template, the usual jigsaw piece.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Template_pool
 *
 * @property projection How the piece adapts to the terrain.
 * @property location The structure template to place.
 * @property processors The processor list applied to every block of the template, by ID or inline.
 * @property overrideLiquidSettings Overrides the waterlogging behavior of the jigsaw structure for this piece.
 */
@Serializable
data class SinglePoolElement(
	var projection: Projection = Projection.RIGID,
	var location: StructureArgument,
	var processors: ProcessorListOrInline = processorListOrInline(ProcessorLists.EMPTY),
	var overrideLiquidSettings: LiquidSettings? = null,
) : TemplatePoolElement()

/**
 * Appends a `single_pool_element` entry placing the [location] template.
 *
 * ```kotlin
 * single(Structures.Village.Plains.Houses.PLAINS_SMALL_HOUSE_1, ProcessorLists.MOSSIFY_10_PERCENT, weight = 3) {
 *     overrideLiquidSettings = LiquidSettings.IGNORE_WATERLOGGING
 * }
 * ```
 *
 * An inline processor list is set in the [block]:
 *
 * ```kotlin
 * single(Structures.EndCity.BRIDGE_END) {
 *     processors = processorListOrInline { gravity(HeightMap.WORLD_SURFACE_WG) }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Template_pool
 */
fun PoolEntriesScope.single(
	location: StructureArgument,
	processors: ProcessorListArgument = ProcessorLists.EMPTY,
	weight: Int = 1,
	projection: Projection = Projection.RIGID,
	block: SinglePoolElement.() -> Unit = {},
) = apply {
	elements += TemplatePoolEntry(
		weight,
		SinglePoolElement(projection, location, processorListOrInline(processors)).apply(block),
	)
}

/**
 * Appends a `single_pool_element` placing the [location] template to the enclosing [ListPoolElement].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Template_pool
 */
fun PoolElementsScope.single(
	location: StructureArgument,
	processors: ProcessorListArgument = ProcessorLists.EMPTY,
	projection: Projection = Projection.RIGID,
	block: SinglePoolElement.() -> Unit = {},
) = apply { elements += SinglePoolElement(projection, location, processorListOrInline(processors)).apply(block) }
