package io.github.ayfri.kore.features.worldgen.processorlist.types

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Keeps the listed blocks of the world untouched, the template never replaces them.
 *
 * Vanilla uses it to stop trail ruins from carving through deepslate.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 *
 * @property value The blocks, or block tags, that cannot be replaced.
 */
@Serializable
data class ProtectedBlocks(
	var value: InlinableList<BlockOrTagArgument> = emptyList(),
) : ProcessorType()

/**
 * Appends a `protected_blocks` processor keeping the blocks of [value] untouched.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
fun ProcessorsScope.protectedBlocks(value: InlinableList<BlockOrTagArgument>) = apply { processors += ProtectedBlocks(value) }

/**
 * Appends a `protected_blocks` processor keeping the given [blocks] untouched.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
fun ProcessorsScope.protectedBlocks(vararg blocks: BlockOrTagArgument) = apply { processors += ProtectedBlocks(blocks.toList()) }
