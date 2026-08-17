package io.github.ayfri.kore.features.worldgen.processorlist.types

import kotlinx.serialization.Serializable

/**
 * Ages the blocks of a template, turning stone bricks into their mossy and cracked variants, cobblestone into mossy
 * cobblestone, and randomly removing some blocks entirely.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 *
 * @property mossiness How aged the blocks look, clamped between `0.0` and `1.0`.
 */
@Serializable
data class BlockAge(
	var mossiness: Double = 0.0,
) : ProcessorType()

/**
 * Appends a `block_age` processor aging the template blocks with the given [mossiness], between `0.0` and `1.0`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
fun ProcessorsScope.blockAge(mossiness: Double = 0.0) = apply { processors += BlockAge(mossiness) }
