package features.worldgen.configuredfeature.configurations

import features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import features.worldgen.configuredfeature.configurations.tree.foliageplacer.FancyFoliagePlacer
import features.worldgen.configuredfeature.configurations.tree.foliageplacer.FoliagePlacer
import features.worldgen.configuredfeature.configurations.tree.layersfeaturesize.LayersFeatureSize
import features.worldgen.configuredfeature.configurations.tree.layersfeaturesize.TwoLayersFeatureSize
import features.worldgen.configuredfeature.configurations.tree.treedecorator.TreeDecorator
import features.worldgen.configuredfeature.configurations.tree.trunkplacer.FancyTrunkPlacer
import features.worldgen.configuredfeature.configurations.tree.trunkplacer.TrunkPlacer
import kotlinx.serialization.Serializable

@Serializable
data class Tree(
	var ignoreVines: Boolean? = null,
	var forceDirt: Boolean? = null,
	var minimumSize: LayersFeatureSize = TwoLayersFeatureSize(),
	var dirtProvider: BlockStateProvider = simpleStateProvider(),
	var trunkProvider: BlockStateProvider = simpleStateProvider(),
	var foliageProvider: BlockStateProvider = simpleStateProvider(),
	var trunkPlacer: TrunkPlacer,
	var foliagePlacer: FoliagePlacer,
	var decorators: List<TreeDecorator> = emptyList(),
) : FeatureConfig()

/** Creates a [Tree] configuration, uses [FancyTrunkPlacer] and [FancyFoliagePlacer] by default. */
fun tree(block: Tree.() -> Unit = {}) = Tree(trunkPlacer = FancyTrunkPlacer(), foliagePlacer = FancyFoliagePlacer()).apply(block)

fun Tree.decorators(decorators: List<TreeDecorator>) {
	this.decorators = decorators
}

fun Tree.decorators(vararg decorators: TreeDecorator) {
	this.decorators = decorators.toList()
}

fun Tree.decorators(block: MutableList<TreeDecorator>.() -> Unit) {
	decorators = buildList(block)
}
