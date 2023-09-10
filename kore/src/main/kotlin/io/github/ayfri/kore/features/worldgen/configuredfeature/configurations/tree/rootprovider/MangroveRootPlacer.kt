package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.rootprovider

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.serializers.InlinableList
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
