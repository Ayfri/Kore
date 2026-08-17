package io.github.ayfri.kore.features.worldgen.processorlist.types

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.data.block.BlockState
import kotlinx.serialization.Serializable

/**
 * Skips the listed block states when placing the template, leaving whatever the world already has at those positions.
 *
 * Vanilla uses it to keep structure blocks and jigsaw blocks out of the placed structure.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 *
 * @property blocks The block states that are not placed.
 */
@Serializable
data class BlockIgnore(
	var blocks: List<BlockState> = emptyList(),
) : ProcessorType()

/**
 * Appends a `block_ignore` processor skipping the given [blocks].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
fun ProcessorsScope.blockIgnore(blocks: List<BlockState>) = apply { processors += BlockIgnore(blocks) }

/**
 * Appends a `block_ignore` processor skipping the given [blocks], each without block-state properties.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
fun ProcessorsScope.blockIgnore(vararg blocks: BlockArgument) =
	apply { processors += BlockIgnore(blocks.map { BlockState(it) }) }
