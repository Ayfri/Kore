package io.github.ayfri.kore.helpers.blocks

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.DataPackStateKey
import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.features.contextproviders.contextIntProvider
import io.github.ayfri.kore.features.predicates.conditions.matchBlock
import io.github.ayfri.kore.features.predicates.providers.add
import io.github.ayfri.kore.features.predicates.providers.conditional
import io.github.ayfri.kore.features.tags.TagEntry
import io.github.ayfri.kore.features.tags.blockTag
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.arguments.types.ContextIntProviderArgument
import io.github.ayfri.kore.helpers.HelpersConstants

private val blockIdProviderKey = DataPackStateKey<ContextIntProviderArgument>("helpers.blockIdProvider")

private val blockIds = Blocks.entries.withIndex().associate { (index, block) -> block.asId() to index + 1 }

/** Number of bits of a block ID, the smallest `k` with `2^k > Blocks.entries.size`, which is also the number of generated block tags. */
val blockIdBits = Int.SIZE_BITS - Blocks.entries.size.countLeadingZeroBits()

/**
 * Returns the ID [blockIdProvider] evaluates for [block]: its 1-based position in the generated [Blocks] enum, `0` for a
 * block Kore doesn't know (a modded block, for instance). Block states and block entity data are ignored.
 *
 * IDs follow the block list of the Minecraft version Kore targets, so they change between versions: compare against
 * `blockId(...)` at build time, never store them.
 *
 * ```
 * blockId(Blocks.ACACIA_BUTTON)                                         // 1
 * blockId(Blocks.OAK_STAIRS(mapOf("half" to "top"))) == blockId(Blocks.OAK_STAIRS)  // true
 * blockId(BlockArgument("ruby_ore", "my_mod"))                          // 0
 * ```
 */
fun blockId(block: BlockArgument) = blockIds[block.asId()] ?: 0

/**
 * Creates once per pack the context int provider evaluating to the [blockId] of the block read by `compute block`, and
 * returns it. Calling it again returns the same provider without generating anything more.
 *
 * The provider sums one `match_block` check per bit of the ID, each against a generated block tag listing the blocks whose
 * ID has that bit set, so every evaluation runs [blockIdBits] checks. Block states are ignored and an unknown block gives `0`.
 * It only means something in `compute block <pos>`: `match_block` is false in any other `compute` context.
 *
 * ```
 * val ids = blockIdProvider()
 * function("block_below") {
 *     execute {
 *         storeResult { score(self(), "block_id") }
 *         run { compute(vec3(0.relativePos, (-1).relativePos, 0.relativePos), ids) }  // compute block ~ ~-1 ~ integer <pack>:kore_block_id
 *     }
 * }
 * ```
 *
 * Then compare the score with `blockId(Blocks.STONE)`, a constant known at build time.
 *
 * Produces `data/<namespace>/context_int_provider/kore_block_id.json` and `data/<namespace>/tags/block/kore_block_id/bit_<k>.json`.
 */
fun DataPack.blockIdProvider() = state(blockIdProviderKey) {
	val tags = List(blockIdBits) { bit ->
		blockTag(HelpersConstants.blockIdTagName(bit)) {
			values = Blocks.entries.filterIndexed { index, _ -> (index + 1) shr bit and 1 == 1 }.map { TagEntry(it.asId()) }
		}
	}

	contextIntProvider(HelpersConstants.blockIdName) {
		add(tags.mapIndexed { bit, tag -> conditional(1 shl bit) { matchBlock(tag) } })
	}
}
