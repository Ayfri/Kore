package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleBlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions.Surface
import io.github.ayfri.kore.generated.arguments.worldgen.types.PlacedFeatureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Configuration for the `vegetation_patch` feature.
 *
 * Covers the floor or the ceiling of a cave with a patch of [groundState], then runs [vegetationFeature] on top of it.
 * Vanilla grows the moss patches of the lush caves with it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#vegetation_patch
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
data class VegetationPatch(
	var surface: Surface,
	var depth: IntProvider = ConstantIntProvider(1),
	var verticalRange: Int = 1,
	var extraBottomBlockChance: Double = 0.0,
	var extraEdgeColumnChance: Double = 0.0,
	var vegetationChance: Double = 0.0,
	var xzRadius: IntProvider = ConstantIntProvider(0),
	var replaceable: InlinableList<BlockOrTagArgument>,
	var groundState: BlockStateProvider = SimpleBlockStateProvider(),
	var vegetationFeature: PlacedFeatureArgument,
) : FeatureConfig(), BlockStateProviderScope, IntProviderScope

/**
 * Creates a `vegetation_patch` configured feature covering the [surface] and running [vegetationFeature] on top,
 * configured in [block].
 *
 * The block state provider and int provider builders are scoped to [block].
 *
 * ```kotlin
 * vegetationPatch(
 *     "moss_patch",
 *     surface = Surface.FLOOR,
 *     replaceable = listOf(Tags.Block.MOSS_REPLACEABLE),
 *     vegetationFeature = PlacedFeatures.PATCH_GRASS_PLAIN,
 * ) {
 *     groundState = simpleStateProvider(Blocks.MOSS_BLOCK)
 *     depth = constant(1)
 *     verticalRange = 5
 *     extraEdgeColumnChance = 0.3
 *     vegetationChance = 0.8
 *     xzRadius = uniform(4, 7)
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#vegetation_patch
 */
fun ConfiguredFeatures.vegetationPatch(
	fileName: String,
	surface: Surface,
	replaceable: InlinableList<BlockOrTagArgument>,
	vegetationFeature: PlacedFeatureArgument,
	block: VegetationPatch.() -> Unit = {},
) = dp.configuredFeature(
	fileName,
	VegetationPatch(surface, replaceable = replaceable, vegetationFeature = vegetationFeature).apply(block),
)
