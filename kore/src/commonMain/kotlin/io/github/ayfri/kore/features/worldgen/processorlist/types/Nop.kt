package io.github.ayfri.kore.features.worldgen.processorlist.types

import kotlinx.serialization.Serializable

/**
 * Does nothing, leaving the template blocks untouched. Useful as a placeholder delegate for [Capped].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
@Serializable
data object Nop : ProcessorType()

/**
 * Appends a `nop` processor, leaving the template blocks untouched.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
fun ProcessorsScope.nop() = apply { processors += Nop }
