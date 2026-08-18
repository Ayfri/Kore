package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.RuleBasedStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.ruleBasedStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
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
 * [belowTrunkProvider] is a [RuleBasedStateProvider] that controls which block is placed beneath the trunk.
 * It is mandatory, defaulting to an empty [ruleBasedStateProvider] which places nothing.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
@Serializable
data class Tree(
	var ignoreVines: Boolean? = null,
	var belowTrunkProvider: BlockStateProvider,
	var minimumSize: LayersFeatureSize = TwoLayersFeatureSize(),
	var trunkProvider: BlockStateProvider = simpleStateProvider(),
	var foliageProvider: BlockStateProvider = simpleStateProvider(),
	var trunkPlacer: TrunkPlacer,
	var foliagePlacer: FoliagePlacer,
	var decorators: List<TreeDecorator> = emptyList(),
) : FeatureConfig()

fun Tree.decorators(decorators: List<TreeDecorator>) {
	this.decorators = decorators
}

fun Tree.decorators(vararg decorators: TreeDecorator) {
	this.decorators = decorators.toList()
}

fun Tree.decorators(block: MutableList<TreeDecorator>.() -> Unit) {
	decorators = buildList(block)
}

/** Creates a [Tree] configuration, uses [FancyTrunkPlacer] and [FancyFoliagePlacer] by default. */
fun ConfiguredFeatures.tree(fileName: String, block: Tree.() -> Unit = {}): ConfiguredFeatureArgument {
	val tree = Tree(
		belowTrunkProvider = ruleBasedStateProvider(),
		trunkPlacer = FancyTrunkPlacer(),
		foliagePlacer = FancyFoliagePlacer(),
	).apply(block)
	val configuredFeature = ConfiguredFeature(fileName, tree)
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
