package io.github.ayfri.kore.features.worldgen.processorlist.types

import kotlinx.serialization.Serializable

/**
 * Replaces every jigsaw block left in the template by its final state block, so no jigsaw block ends up in the world.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
@Serializable
data object JigsawReplacement : ProcessorType()

/**
 * Appends a `jigsaw_replacement` processor turning leftover jigsaw blocks into their final state block.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
fun ProcessorsScope.jigsawReplacement() = apply { processors += JigsawReplacement }
