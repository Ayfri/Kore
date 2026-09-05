package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.RuleBasedStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer.FancyFoliagePlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer.FoliagePlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.layersfeaturesize.LayersFeatureSize
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.layersfeaturesize.TwoLayersFeatureSize
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator.TreeDecorator
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer.FancyTrunkPlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer.TrunkPlacer
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

/**
 * Configuration for the `tree` feature.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property ignoreVines Whether the tree grows through the vines instead of being blocked by them.
 * @property belowTrunkProvider The [RuleBasedStateProvider] placing a block beneath the trunk, such as the podzol of
 * the spruce trees, placing nothing when it has no rule.
 * @property minimumSize The vertical space the tree needs to grow.
 * @property trunkProvider The block states making up the trunk.
 * @property foliageProvider The block states making up the foliage.
 * @property trunkPlacer The shape of the trunk.
 * @property foliagePlacer The shape of the foliage.
 * @property decorators The decorators run once the tree is placed, such as the beehives and the cocoa beans.
 */
@Serializable
data class Tree(
	var ignoreVines: Boolean? = null,
	var belowTrunkProvider: BlockStateProvider = RuleBasedStateProvider(),
	var minimumSize: LayersFeatureSize = TwoLayersFeatureSize(),
	var trunkProvider: BlockStateProvider = SimpleStateProvider(),
	var foliageProvider: BlockStateProvider = SimpleStateProvider(),
	var trunkPlacer: TrunkPlacer,
	var foliagePlacer: FoliagePlacer,
	var decorators: List<TreeDecorator> = emptyList(),
) : FeatureConfig(), BlockStateProviderScope

/** Sets [Tree.decorators] to [decorators]. */
fun Tree.decorators(decorators: List<TreeDecorator>) {
	this.decorators = decorators
}

/** Sets [Tree.decorators] to [decorators]. */
fun Tree.decorators(vararg decorators: TreeDecorator) {
	this.decorators = decorators.toList()
}

/**
 * Sets [Tree.decorators] to the decorators appended in [block].
 *
 * ```kotlin
 * tree("oak") {
 *     decorators {
 *         alterGround { rule(simpleStateProvider(Blocks.PODZOL)) { solid() } }
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun Tree.decorators(block: MutableList<TreeDecorator>.() -> Unit) {
	decorators = buildList(block)
}

/**
 * Creates a `tree` configured feature, using a [FancyTrunkPlacer] and a [FancyFoliagePlacer] by default.
 *
 * The block state provider builders are scoped to [block].
 *
 * ```kotlin
 * tree("oak") {
 *     trunkProvider = simpleStateProvider(Blocks.OAK_LOG)
 *     foliageProvider = simpleStateProvider(Blocks.OAK_LEAVES)
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/configured_feature/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun ConfiguredFeatures.tree(fileName: String, block: Tree.() -> Unit = {}): ConfiguredFeatureArgument {
	val tree = Tree(
		trunkPlacer = FancyTrunkPlacer(),
		foliagePlacer = FancyFoliagePlacer(),
	).apply(block)
	val configuredFeature = ConfiguredFeature(fileName, tree)
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
