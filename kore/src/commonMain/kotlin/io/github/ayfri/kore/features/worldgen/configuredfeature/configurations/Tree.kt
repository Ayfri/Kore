package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.RuleBasedStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer.FancyFoliagePlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer.FoliagePlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.layersfeaturesize.LayersFeatureSize
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.layersfeaturesize.TwoLayersFeatureSize
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.rootprovider.RootPlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator.TreeDecorator
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer.FancyTrunkPlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer.TrunkPlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `tree` feature.
 *
 * Grows a tree made of a trunk, foliage and optional decorations. Vanilla grows all its trees with it, from the oaks
 * and the spruces to the azaleas, the mangroves and the jungle bushes.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property ignoreVines Whether the tree grows even when vines block it, `false` in vanilla when `null`.
 * @property belowTrunkProvider The [RuleBasedStateProvider] replacing the block below the trunk, such as the dirt
 * under most trees, placing nothing when it has no rule.
 * @property minimumSize The width the tree needs at each height to be placed.
 * @property trunkProvider The block state provider of the trunk, which needs an `axis` property with a fancy trunk
 * placer.
 * @property foliageProvider The block state provider of the foliage.
 * @property rootPlacer The placer of the roots, such as the mangrove roots, none when `null`.
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
	var rootPlacer: RootPlacer? = null,
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
 * Creates a `tree` configured feature, using a [FancyTrunkPlacer] and a [FancyFoliagePlacer] unless [block] sets
 * others.
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
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun ConfiguredFeatures.tree(fileName: String, block: Tree.() -> Unit = {}) = dp.configuredFeature(
	fileName,
	Tree(
		trunkPlacer = FancyTrunkPlacer(),
		foliagePlacer = FancyFoliagePlacer(),
	).apply(block),
)
