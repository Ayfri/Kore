package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import io.github.ayfri.kore.generated.Tags
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Configuration for the `geode` feature.
 *
 * Places a hollow sphere made of [GeodeBlocks.filling], inner, middle and outer shells, with a crack opening it to the
 * outside. Vanilla builds the amethyst geodes with it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#geode
 *
 * @property blocks The block states of the shells and the blocks the geode never replaces or refuses to generate in.
 * @property layers The thickness of each shell.
 * @property crack The crack opening the geode.
 * @property noiseMultiplier Between `0` and `1`, how much noise distorts the shells, `0.05` when `null`.
 * @property usePotentialPlacementsChance Between `0` and `1`, the chance of placing an inner placement on a block of
 * the inner layer, `0.35` when `null`.
 * @property useAlternateLayer0Chance Between `0` and `1`, the chance of replacing an inner layer block with an
 * alternate inner layer block, `0.0` when `null`.
 * @property placementsRequireLayer0Alternate Whether the inner placements can only go on an alternate inner layer
 * block, `true` when `null`.
 * @property outerWallDistance Offset on each coordinate of the center from the feature start, `1` to `20`, a uniform
 * `4` to `5` when `null`.
 * @property distributionPoints Number of points the shells are distributed around, `1` to `20`, a uniform `3` to `4`
 * when `null`.
 * @property pointOffset Offset of the distribution points, `1` to `10`, a uniform `1` to `2` when `null`.
 * @property minGenOffset The minimum Chebyshev distance between a block and the center, `-16` when `null`.
 * @property maxGenOffset The maximum Chebyshev distance between a block and the center, `16` when `null`.
 * @property invalidBlocksThreshold The number of invalid blocks found near the center above which the geode is not
 * generated.
 */
@Serializable
data class Geode(
	var blocks: GeodeBlocks = GeodeBlocks(),
	var layers: GeodeLayers = GeodeLayers(),
	var crack: GeodeCrack = GeodeCrack(),
	var noiseMultiplier: Double? = null,
	var usePotentialPlacementsChance: Double? = null,
	var useAlternateLayer0Chance: Double? = null,
	var placementsRequireLayer0Alternate: Boolean? = null,
	var outerWallDistance: IntProvider? = null,
	var distributionPoints: IntProvider? = null,
	var pointOffset: IntProvider? = null,
	var minGenOffset: Int? = null,
	var maxGenOffset: Int? = null,
	var invalidBlocksThreshold: Int = 0,
) : FeatureConfig(), IntProviderScope

/**
 * The blocks of a [Geode]: its shells and the blocks it never replaces or refuses to generate in.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#geode
 *
 * @property filling The blocks filling the hollow center, air in vanilla.
 * @property innerLayer The blocks of the innermost shell, amethyst blocks in vanilla.
 * @property alternateInnerLayer The blocks replacing [innerLayer] with a chance of [Geode.useAlternateLayer0Chance],
 * budding amethyst in vanilla.
 * @property middleLayer The blocks of the middle shell, calcite in vanilla.
 * @property outerLayer The blocks of the outermost shell, smooth basalt in vanilla.
 * @property innerPlacements The block states placed on the inner walls, at least one: the amethyst buds and cluster in
 * vanilla.
 * @property cannotReplace A block, a block tag or a list of blocks the geode never replaces,
 * `#minecraft:features_cannot_replace` by default.
 * @property invalidBlocks A block, a block tag or a list of blocks whose presence cancels the geode, such as the
 * fluids, `#minecraft:geode_invalid_blocks` by default.
 */
@Serializable
data class GeodeBlocks(
	var filling: BlockStateProvider = SimpleStateProvider(),
	var innerLayer: BlockStateProvider = SimpleStateProvider(),
	var alternateInnerLayer: BlockStateProvider = SimpleStateProvider(),
	var middleLayer: BlockStateProvider = SimpleStateProvider(),
	var outerLayer: BlockStateProvider = SimpleStateProvider(),
	var innerPlacements: List<BlockState> = emptyList(),
	var cannotReplace: InlinableList<BlockOrTagArgument> = listOf(Tags.Block.FEATURES_CANNOT_REPLACE),
	var invalidBlocks: InlinableList<BlockOrTagArgument> = listOf(Tags.Block.GEODE_INVALID_BLOCKS),
) : BlockStateProviderScope

/**
 * The thickness of each shell of a [Geode], from `0.01` to `50`: larger values make larger geodes, and the scale isn't
 * linear.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#geode
 *
 * @property filling The thickness of the hollow center, `1.7` when `null`.
 * @property innerLayer The thickness of the inner shell, `2.2` when `null`.
 * @property middleLayer The thickness of the middle shell, `3.2` when `null`.
 * @property outerLayer The thickness of the outer shell, `4.2` when `null`.
 */
@Serializable
data class GeodeLayers(
	var filling: Double? = null,
	var innerLayer: Double? = null,
	var middleLayer: Double? = null,
	var outerLayer: Double? = null,
)

/**
 * The crack of a [Geode], the opening that lets the light in.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#geode
 *
 * @property generateCrackChance Between `0` and `1`, the chance of generating the crack, `1.0` when `null`.
 * @property baseCrackSize The base size of the crack, `0` to `5`, `2` when `null`.
 * @property crackPointOffset The offset of the crack points, `0` to `10`, `2` when `null`.
 */
@Serializable
data class GeodeCrack(
	var generateCrackChance: Double? = null,
	var baseCrackSize: Double? = null,
	var crackPointOffset: Double? = null,
)

/**
 * Configures [Geode.blocks], the block state providers making up each shell.
 *
 * The block state provider builders are scoped to [block].
 *
 * ```kotlin
 * geode("amethyst_geode") {
 *     blocks {
 *         filling = simpleStateProvider(Blocks.AIR)
 *         innerLayer = simpleStateProvider(Blocks.AMETHYST_BLOCK)
 *         middleLayer = simpleStateProvider(Blocks.CALCITE)
 *         outerLayer = simpleStateProvider(Blocks.SMOOTH_BASALT)
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#geode
 */
fun Geode.blocks(block: GeodeBlocks.() -> Unit = {}) = blocks.apply(block)

/**
 * Configures [Geode.layers], the thickness of each shell.
 *
 * ```kotlin
 * geode("amethyst_geode") {
 *     layers {
 *         filling = 1.7
 *         outerLayer = 4.2
 *     }
 * }
 * ```
 */
fun Geode.layers(block: GeodeLayers.() -> Unit = {}) = layers.apply(block)

/**
 * Configures [Geode.crack], the opening letting the light into the geode.
 *
 * ```kotlin
 * geode("amethyst_geode") {
 *     crack { generateCrackChance = 0.95 }
 * }
 * ```
 */
fun Geode.crack(block: GeodeCrack.() -> Unit = {}) = crack.apply(block)

/**
 * Creates a `geode` configured feature, the layered hollow spheres the amethyst geodes are made of.
 *
 * The int provider builders are scoped to [block].
 *
 * ```kotlin
 * geode("amethyst_geode") {
 *     blocks {
 *         innerLayer = simpleStateProvider(Blocks.AMETHYST_BLOCK)
 *         alternateInnerLayer = simpleStateProvider(Blocks.BUDDING_AMETHYST)
 *         middleLayer = simpleStateProvider(Blocks.CALCITE)
 *         outerLayer = simpleStateProvider(Blocks.SMOOTH_BASALT)
 *         innerPlacements = listOf(blockState(Blocks.AMETHYST_CLUSTER))
 *     }
 *     useAlternateLayer0Chance = 0.083
 *     outerWallDistance = uniform(4, 6)
 *     invalidBlocksThreshold = 1
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#geode
 */
fun ConfiguredFeatures.geode(fileName: String, block: Geode.() -> Unit = {}) =
	dp.configuredFeature(fileName, Geode().apply(block))
