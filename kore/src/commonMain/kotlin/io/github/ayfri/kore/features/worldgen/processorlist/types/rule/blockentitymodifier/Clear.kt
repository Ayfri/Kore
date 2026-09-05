package io.github.ayfri.kore.features.worldgen.processorlist.types.rule.blockentitymodifier

import io.github.ayfri.kore.features.worldgen.processorlist.types.rule.ProcessorRule
import kotlinx.serialization.Serializable

/**
 * Drops the block entity data of the template, the placed block starts with a fresh block entity.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
@Serializable
data object Clear : BlockEntityModifier()

/**
 * Creates a `clear` block entity modifier, dropping the block entity data of the template.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
fun ProcessorRule.clear() = Clear
