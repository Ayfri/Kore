package io.github.ayfri.kore.features.worldgen.processorlist.types.rule.blockentitymodifier

import io.github.ayfri.kore.features.worldgen.processorlist.types.rule.ProcessorRule
import kotlinx.serialization.Serializable

/**
 * Keeps the block entity data of the template untouched. Same behaviour as leaving
 * [ProcessorRule.blockEntityModifier] to `null`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
@Serializable
data object Passthrough : BlockEntityModifier()

/**
 * Creates a `passthrough` block entity modifier, keeping the block entity data of the template untouched.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
fun ProcessorRule.passthrough() = Passthrough
