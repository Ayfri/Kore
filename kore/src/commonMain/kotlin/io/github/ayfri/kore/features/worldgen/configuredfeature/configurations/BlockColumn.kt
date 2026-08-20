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
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data class BlockColumn(
	var direction: Direction,
	var allowedPlacement: BlockPredicate = True,
	var prioritizeTip: Boolean = false,
	var layers: List<Layer> = emptyList(),
) : FeatureConfig(), BlockPredicateScope

@Serializable
data class Layer(
	var height: IntProvider = constant(0),
	var provider: BlockStateProvider = simpleStateProvider(),
)

fun BlockColumn.layer(height: IntProvider = constant(0), provider: BlockStateProvider = simpleStateProvider()) {
	layers += Layer(height, provider)
}

fun BlockColumn.layers(list: List<Layer>) = run { layers = list }
fun BlockColumn.layers(vararg layers: Layer) = run { this.layers = layers.toList() }
fun BlockColumn.layers(block: MutableList<Layer>.() -> Unit) = run { layers = buildList(block) }

fun MutableList<Layer>.layer(height: IntProvider = constant(0), provider: BlockStateProvider = simpleStateProvider()) {
	this += Layer(height, provider)
}

/**
 * Creates a `block_column` configured feature, stacking [BlockColumn.layers] along [direction].
 *
 * The block predicate builders are scoped to [block].
 *
 * ```kotlin
 * blockColumn("cave_vines", direction = Direction.DOWN) {
 *     allowedPlacement { replaceable() }
 *     layers {
 *         layer(constant(3), simpleStateProvider(Blocks.CAVE_VINES_PLANT))
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
