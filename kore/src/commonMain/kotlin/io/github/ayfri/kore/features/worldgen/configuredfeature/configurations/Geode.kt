package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleStateProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import io.github.ayfri.kore.generated.Tags
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

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
 * The block state providers making up each shell of a [Geode].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#geode
 *
 * @property filling The blocks filling the hollow center.
 * @property innerLayer The blocks of the innermost shell.
 * @property alternateInnerLayer The blocks replacing [innerLayer] with a chance of [Geode.useAlternateLayer0Chance].
 * @property middleLayer The blocks of the middle shell.
 * @property outerLayer The blocks of the outermost shell.
 * @property innerPlacements The blocks placed on the inner walls, such as the amethyst clusters.
 * @property cannotReplace The blocks the geode never replaces.
 * @property invalidBlocks The blocks whose presence cancels the geode, such as the fluids.
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

@Serializable
data class GeodeLayers(
	var filling: Double? = null,
	var innerLayer: Double? = null,
	var middleLayer: Double? = null,
	var outerLayer: Double? = null,
)

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

/** Configures [Geode.layers], the thickness of each shell. */
fun Geode.layers(block: GeodeLayers.() -> Unit = {}) = layers.apply(block)

/** Configures [Geode.crack], the opening letting the light into the geode. */
fun Geode.crack(block: GeodeCrack.() -> Unit = {}) = crack.apply(block)

/**
 * Creates a `geode` configured feature, the layered hollow spheres the amethyst geodes are made of.
 *
 * Produces `data/<namespace>/worldgen/configured_feature/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#geode
 */
fun ConfiguredFeatures.geode(fileName: String, block: Geode.() -> Unit = {}): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, Geode().apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
