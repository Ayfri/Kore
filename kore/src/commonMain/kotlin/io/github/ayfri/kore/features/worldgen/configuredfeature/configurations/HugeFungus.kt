package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicateScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicatesScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
import io.github.ayfri.kore.features.worldgen.blockpredicate.blockPredicate
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleStateProvider
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

/**
 * Configuration for the `huge_fungus` feature, the crimson and warped fungi of the nether forests.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#huge_fungus
 *
 * @property hatState The block states making up the hat.
 * @property decorState The block states of the shroomlights decorating the hat.
 * @property stemState The block states making up the stem.
 * @property validBaseBlock The block the fungus has to grow on, given as a provider whose state is compared.
 * @property replaceableBlocks The blocks the fungus may grow through.
 * @property planted Whether the fungus grew from a planted fungus rather than from world generation.
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
 * Produces `data/<namespace>/worldgen/configured_feature/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#huge_fungus
 */
fun ConfiguredFeatures.hugeFungus(fileName: String, block: HugeFungus.() -> Unit = {}): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, HugeFungus().apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}

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
