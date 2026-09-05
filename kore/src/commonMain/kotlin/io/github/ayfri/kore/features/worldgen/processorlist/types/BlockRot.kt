package io.github.ayfri.kore.features.worldgen.processorlist.types

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Randomly removes blocks of the template, as if the structure had rotted away.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 *
 * @property integrity The chance for a block to be kept, between `0.0` (nothing is placed) and `1.0` (everything is placed).
 * @property rottableBlocks The blocks that may rot, all of them when `null`.
 */
@Serializable
data class BlockRot(
	var integrity: Double = 1.0,
	var rottableBlocks: InlinableList<BlockOrTagArgument>? = null,
) : ProcessorType()

/**
 * Appends a `block_rot` processor keeping each block of [rottableBlocks] with a chance of [integrity], between `0.0`
 * and `1.0`. Every block may rot when [rottableBlocks] is `null`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
fun ProcessorsScope.blockRot(integrity: Double = 1.0, rottableBlocks: InlinableList<BlockOrTagArgument>? = null) =
	apply { processors += BlockRot(integrity, rottableBlocks) }

/**
 * Appends a `block_rot` processor keeping each block of [rottableBlocks] with a chance of [integrity], between `0.0`
 * and `1.0`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
fun ProcessorsScope.blockRot(integrity: Double = 1.0, vararg rottableBlocks: BlockOrTagArgument) =
	apply { processors += BlockRot(integrity, rottableBlocks.toList()) }
