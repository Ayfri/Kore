package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicateScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicatesScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
import io.github.ayfri.kore.features.worldgen.blockpredicate.blockPredicate
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `huge_fungus` feature.
 *
 * Grows a huge fungus, a stem topped with a hat and decorated with shroomlights. Vanilla grows the crimson and warped
 * fungi of the nether forests with it, in a generated and a planted variant each.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#huge_fungus
 *
 * @property hatState The block making up the hat.
 * @property decorState The block decorating the hat, shroomlights in vanilla.
 * @property stemState The block making up the stem.
 * @property validBaseBlock The block the fungus has to grow on, nylium in vanilla.
 * @property replaceableBlocks The blocks the fungus may replace as it grows.
 * @property planted Whether the fungus is planted, `false` when `null`. A fungus that isn't planted can't exceed the
 * world ceiling and doesn't drop items from the blocks it replaces.
 */
@Serializable
data class HugeFungus(
	var hatState: BlockStateProvider = SimpleStateProvider(),
	var decorState: BlockStateProvider = SimpleStateProvider(),
	var stemState: BlockStateProvider = SimpleStateProvider(),
	var validBaseBlock: BlockStateProvider = SimpleStateProvider(),
	var replaceableBlocks: BlockPredicate = True,
	var planted: Boolean? = null,
) : FeatureConfig(), BlockPredicateScope, BlockStateProviderScope

/**
 * Creates a `huge_fungus` configured feature.
 *
 * The block predicate and block state provider builders are scoped to [block].
 *
 * ```kotlin
 * hugeFungus("crimson_fungus") {
 *     hatState = simpleStateProvider(Blocks.NETHER_WART_BLOCK)
 *     stemState = simpleStateProvider(Blocks.CRIMSON_STEM)
 *     validBaseBlock = simpleStateProvider(Blocks.CRIMSON_NYLIUM)
 *     replaceableBlocks { matchingBlockTag(Tags.Block.REPLACEABLE_BY_TREES) }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#huge_fungus
 */
fun ConfiguredFeatures.hugeFungus(fileName: String, block: HugeFungus.() -> Unit = {}) =
	dp.configuredFeature(fileName, HugeFungus().apply(block))

/**
 * Sets [HugeFungus.replaceableBlocks] to the predicate built in [block], the blocks the fungus may grow through.
 *
 * A lone predicate is used as-is, several of them are wrapped in an `all_of`.
 *
 * ```kotlin
 * hugeFungus("crimson_fungus") {
 *     replaceableBlocks { matchingBlockTag(Tags.Block.REPLACEABLE_BY_TREES) }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun HugeFungus.replaceableBlocks(block: BlockPredicatesScope.() -> Unit) {
	replaceableBlocks = blockPredicate(block)
}
