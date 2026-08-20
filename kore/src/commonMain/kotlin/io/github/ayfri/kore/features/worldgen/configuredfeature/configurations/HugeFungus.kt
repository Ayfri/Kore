package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicateScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicatesScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
import io.github.ayfri.kore.features.worldgen.blockpredicate.blockPredicate
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data class HugeFungus(
	var hatState: BlockStateProvider = simpleStateProvider(),
	var decorState: BlockStateProvider = simpleStateProvider(),
	var stemState: BlockStateProvider = simpleStateProvider(),
	var validBaseBlock: BlockStateProvider = simpleStateProvider(),
	var replaceableBlocks: BlockPredicate = True,
	var planted: Boolean? = null,
) : FeatureConfig(), BlockPredicateScope

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
