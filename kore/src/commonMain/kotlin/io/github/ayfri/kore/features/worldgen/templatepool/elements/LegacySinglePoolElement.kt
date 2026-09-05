package io.github.ayfri.kore.features.worldgen.templatepool.elements

import io.github.ayfri.kore.features.worldgen.structures.types.jigsaw.LiquidSettings
import io.github.ayfri.kore.features.worldgen.templatepool.Projection
import io.github.ayfri.kore.features.worldgen.templatepool.TemplatePoolEntry
import io.github.ayfri.kore.generated.ProcessorLists
import io.github.ayfri.kore.generated.arguments.worldgen.types.ProcessorListArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.StructureArgument
import kotlinx.serialization.Serializable

/**
 * Places a structure template like [SinglePoolElement], but keeps the blocks already in the world where the template
 * has air, instead of clearing them.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Template_pool
 *
 * @property projection How the piece adapts to the terrain.
 * @property location The structure template to place.
 * @property processors The processor list applied to every block of the template.
 * @property overrideLiquidSettings Overrides the waterlogging behavior of the jigsaw structure for this piece.
 */
@Serializable
data class LegacySinglePoolElement(
	var projection: Projection = Projection.RIGID,
	var location: StructureArgument,
	var processors: ProcessorListArgument = ProcessorLists.EMPTY,
	var overrideLiquidSettings: LiquidSettings? = null,
) : TemplatePoolElement()

/**
 * Appends a `legacy_single_pool_element` entry placing the [location] template.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Template_pool
 */
fun PoolEntriesScope.legacySingle(
	location: StructureArgument,
	processors: ProcessorListArgument = ProcessorLists.EMPTY,
	weight: Int = 1,
	projection: Projection = Projection.RIGID,
	block: LegacySinglePoolElement.() -> Unit = {},
) = apply { elements += TemplatePoolEntry(weight, LegacySinglePoolElement(projection, location, processors).apply(block)) }

/**
 * Appends a `legacy_single_pool_element` placing the [location] template to the enclosing [ListPoolElement].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Template_pool
 */
fun PoolElementsScope.legacySingle(
	location: StructureArgument,
	processors: ProcessorListArgument = ProcessorLists.EMPTY,
	projection: Projection = Projection.RIGID,
	block: LegacySinglePoolElement.() -> Unit = {},
) = apply { elements += LegacySinglePoolElement(projection, location, processors).apply(block) }
