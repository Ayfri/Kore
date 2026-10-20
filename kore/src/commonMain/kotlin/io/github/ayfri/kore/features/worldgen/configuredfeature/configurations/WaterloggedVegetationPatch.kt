package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions.Surface
import io.github.ayfri.kore.generated.arguments.worldgen.types.PlacedFeatureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Configuration for the `waterlogged_vegetation_patch` feature.
 *
 * Like `vegetation_patch`, but the patch holds water, forming a pool. Vanilla digs the clay pools of the lush caves
 * with it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#waterlogged_vegetation_patch
 *
 * @property surface Whether the patch covers the [Surface.FLOOR] or the [Surface.CEILING].
 * @property depth Number of ground blocks placed in each column, `1` to `128`.
 * @property verticalRange How far up or down from the placement position each column searches for a surface, `1` to
 * `256`.
 * @property extraBottomBlockChance Chance, `0` to `1`, to add one more block to the depth of a column.
 * @property extraEdgeColumnChance Chance, `0` to `1`, to add a column next to the edge of the patch.
 * @property vegetationChance Chance, `0` to `1`, to run [vegetationFeature] on a column.
 * @property xzRadius Horizontal radius of the patch, an int provider, a uniform `4` to `7` in vanilla.
 * @property replaceable A block, a block tag or a list of blocks the ground can replace.
 * @property groundState The block state provider of the ground.
 * @property vegetationFeature The placed feature run on top of the columns.
 */
@Serializable
data class WaterloggedVegetationPatch(
	var surface: Surface,
	var depth: IntProvider = ConstantIntProvider(1),
	var verticalRange: Int = 1,
	var extraBottomBlockChance: Double = 0.0,
	var extraEdgeColumnChance: Double = 0.0,
	var vegetationChance: Double = 0.0,
	var xzRadius: IntProvider = ConstantIntProvider(0),
	var replaceable: InlinableList<BlockOrTagArgument>,
	var groundState: BlockStateProvider = SimpleStateProvider(),
	var vegetationFeature: PlacedFeatureArgument,
) : FeatureConfig(), BlockStateProviderScope, IntProviderScope

/**
 * Creates a `waterlogged_vegetation_patch` configured feature covering the [surface] and running [vegetationFeature] on
 * top, configured in [block].
 *
 * The block state provider and int provider builders are scoped to [block].
 *
 * ```kotlin
 * waterloggedVegetationPatch(
 *     "clay_pool",
 *     surface = Surface.FLOOR,
 *     replaceable = listOf(Tags.Block.LUSH_GROUND_REPLACEABLE),
 *     vegetationFeature = PlacedFeatures.PATCH_GRASS_PLAIN,
 * ) {
 *     groundState = simpleStateProvider(Blocks.CLAY)
 *     depth = constant(3)
 *     verticalRange = 5
 *     extraBottomBlockChance = 0.8
 *     extraEdgeColumnChance = 0.7
 *     vegetationChance = 0.1
 *     xzRadius = uniform(4, 7)
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#waterlogged_vegetation_patch
 */
fun ConfiguredFeatures.waterloggedVegetationPatch(
	fileName: String,
	surface: Surface,
	replaceable: InlinableList<BlockOrTagArgument>,
	vegetationFeature: PlacedFeatureArgument,
	block: WaterloggedVegetationPatch.() -> Unit = {},
) = dp.configuredFeature(
	fileName,
	WaterloggedVegetationPatch(surface, replaceable = replaceable, vegetationFeature = vegetationFeature).apply(block),
)
