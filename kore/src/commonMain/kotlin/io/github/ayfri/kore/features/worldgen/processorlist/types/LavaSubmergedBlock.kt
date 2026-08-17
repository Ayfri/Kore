package io.github.ayfri.kore.features.worldgen.processorlist.types

import kotlinx.serialization.Serializable

/**
 * Waterlogs, or rather lava-logs, the template blocks that replace lava, filling the empty positions with lava so the
 * structure does not leave air pockets in a lava lake.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
@Serializable
data object LavaSubmergedBlock : ProcessorType()

/**
 * Appends a `lava_submerged_block` processor keeping lava around the blocks placed inside a lava lake.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
fun ProcessorsScope.lavaSubmergedBlock() = apply { processors += LavaSubmergedBlock }
