package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicateScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicatesScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
import io.github.ayfri.kore.features.worldgen.blockpredicate.blockPredicate
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.Direction
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleStateProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

/**
 * Configuration for the `block_column` feature, stacking [layers] one after the other along [direction].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#block_column
 *
 * @property direction The direction the layers are stacked in.
 * @property allowedPlacement The predicate every position of the column has to pass.
 * @property prioritizeTip Whether a column that does not fit is trimmed from its base instead of from its tip.
 * @property layers The layers, stacked in order starting from the placement position.
 */
@Serializable
data class BlockColumn(
	var direction: Direction,
	var allowedPlacement: BlockPredicate = True,
	var prioritizeTip: Boolean = false,
	var layers: List<Layer> = emptyList(),
) : FeatureConfig(), BlockPredicateScope

/**
 * A single layer of a [BlockColumn]: [height] blocks given by [provider].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#block_column
 *
 * @property height The amount of blocks this layer is made of.
 * @property provider The block states making up this layer.
 */
@Serializable
data class Layer(
	var height: IntProvider = constant(0),
	var provider: BlockStateProvider = SimpleStateProvider(),
) : BlockStateProviderScope

/**
 * Builder scope for declaring the layers of a [BlockColumn] via [layers].
 *
 * [layer] is an extension on this class, so it only resolves inside a `layers { }` block.
 *
 * @property layers The layers appended so far.
 */
class LayersScope {
	val layers = mutableListOf<Layer>()
}

/**
 * Sets [BlockColumn.layers] to the layers declared in [block].
 *
 * ```kotlin
 * blockColumn("cave_vines", direction = Direction.DOWN) {
 *     layers {
 *         layer(constant(3)) { provider = simpleStateProvider(Blocks.CAVE_VINES_PLANT) }
 *         layer(constant(1)) { provider = simpleStateProvider(Blocks.CAVE_VINES) }
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#block_column
 */
fun BlockColumn.layers(block: LayersScope.() -> Unit) {
	layers = LayersScope().apply(block).layers
}

/** Sets [BlockColumn.layers] to [list]. */
fun BlockColumn.layers(list: List<Layer>) = run { layers = list }

/** Sets [BlockColumn.layers] to [layers]. */
fun BlockColumn.layers(vararg layers: Layer) = run { this.layers = layers.toList() }

/**
 * Appends a layer of [height] blocks, its block state provider being set in [block].
 *
 * The block state provider builders are scoped to [block].
 *
 * ```kotlin
 * layers {
 *     layer(constant(3)) { provider = simpleStateProvider(Blocks.CAVE_VINES_PLANT) }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#block_column
 */
fun LayersScope.layer(height: IntProvider = constant(0), block: Layer.() -> Unit = {}) {
	layers += Layer(height).apply(block)
}

/** Appends a layer of [height] blocks made of [provider]. */
fun LayersScope.layer(height: IntProvider = constant(0), provider: BlockStateProvider) {
	layers += Layer(height, provider)
}

/**
 * Creates a `block_column` configured feature, stacking [BlockColumn.layers] along [direction].
 *
 * The block predicate builders are scoped to [block], the block state provider ones to each `layer { }`.
 *
 * ```kotlin
 * blockColumn("cave_vines", direction = Direction.DOWN) {
 *     allowedPlacement { replaceable() }
 *     layers {
 *         layer(constant(3)) { provider = simpleStateProvider(Blocks.CAVE_VINES_PLANT) }
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/configured_feature/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#block_column
 */
fun ConfiguredFeatures.blockColumn(
	fileName: String,
	direction: Direction,
	prioritizeTip: Boolean = false,
	layers: List<Layer> = emptyList(),
	block: BlockColumn.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(
		fileName,
		BlockColumn(direction, prioritizeTip = prioritizeTip, layers = layers).apply(block),
	)
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}

/**
 * Sets [BlockColumn.allowedPlacement] to the predicate built in [block], the blocks the column may be placed in.
 *
 * A lone predicate is used as-is, several of them are wrapped in an `all_of`.
 *
 * ```kotlin
 * blockColumn("cave_vines", direction = Direction.DOWN) {
 *     allowedPlacement { replaceable() }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun BlockColumn.allowedPlacement(block: BlockPredicatesScope.() -> Unit) {
	allowedPlacement = blockPredicate(block)
}
