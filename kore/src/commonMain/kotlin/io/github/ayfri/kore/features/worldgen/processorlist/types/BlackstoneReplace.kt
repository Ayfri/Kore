package io.github.ayfri.kore.features.worldgen.processorlist.types

import kotlinx.serialization.Serializable

/**
 * Replaces the blocks of a bastion template with their blackstone counterparts, the way vanilla bastion remnants
 * age: gilded blackstone, cracked polished blackstone bricks and blackstone rubble.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
@Serializable
data object BlackstoneReplace : ProcessorType()

/**
 * Appends a `blackstone_replace` processor, replacing template blocks with their blackstone counterparts.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
fun ProcessorsScope.blackstoneReplace() = apply { processors += BlackstoneReplace }
