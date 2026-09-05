package io.github.ayfri.kore.features.worldgen.processorlist.types

import io.github.ayfri.kore.features.worldgen.HeightMap
import kotlinx.serialization.Serializable

/**
 * Drops every block of the template down to the height given by a heightmap, so the structure follows the terrain.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 *
 * @property heightmap The heightmap the blocks are moved to.
 * @property offset How many blocks to move up from the heightmap height.
 */
@Serializable
data class Gravity(
	var heightmap: HeightMap = HeightMap.WORLD_SURFACE_WG,
	var offset: Int = 0,
) : ProcessorType()

/**
 * Appends a `gravity` processor dropping the template blocks onto [heightmap], moved up by [offset] blocks.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
fun ProcessorsScope.gravity(heightmap: HeightMap = HeightMap.WORLD_SURFACE_WG, offset: Int = 0) =
	apply { processors += Gravity(heightmap, offset) }
