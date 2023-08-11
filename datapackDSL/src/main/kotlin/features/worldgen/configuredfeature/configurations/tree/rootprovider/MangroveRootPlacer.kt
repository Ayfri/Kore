package features.worldgen.configuredfeature.configurations.tree.rootprovider

import arguments.types.BlockOrTagArgument
import features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import features.worldgen.intproviders.IntProvider
import features.worldgen.intproviders.constant
import serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class MangroveRootPlacer(
	override var rootProvider: BlockStateProvider = simpleStateProvider(),
	override var trunkOffsetY: IntProvider = constant(0),
	override var aboveRootProvider: AboveRootPlacement? = null,
	var mangroveRootPlacement: MangroveRootPlacement = MangroveRootPlacement(),
) : RootPlacer()

@Serializable
data class MangroveRootPlacement(
	var maxRootWidth: Int = 0,
	var maxRootLength: Int = 0,
	var randomSkewChance: Double = 0.0,
	var canGrowTrough: InlinableList<BlockOrTagArgument> = emptyList(),
	var muddyRootsIn: InlinableList<BlockOrTagArgument> = emptyList(),
	var muddyRootsProvider: BlockStateProvider = simpleStateProvider(),
)

fun mangroveRootPlacer(
	rootProvider: BlockStateProvider = simpleStateProvider(),
	trunkOffsetY: IntProvider = constant(0),
	aboveRootProvider: AboveRootPlacement? = null,
	mangroveRootPlacement: MangroveRootPlacement = MangroveRootPlacement(),
	block: MangroveRootPlacer.() -> Unit = {},
) = MangroveRootPlacer(rootProvider, trunkOffsetY, aboveRootProvider, mangroveRootPlacement).apply(block)

fun MangroveRootPlacer.mangroveRootPlacement(block: MangroveRootPlacement.() -> Unit = {}) = MangroveRootPlacement().apply(block)
