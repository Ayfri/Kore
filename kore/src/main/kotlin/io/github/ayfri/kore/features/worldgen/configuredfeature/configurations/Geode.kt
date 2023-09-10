package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.arguments.types.resources.tagged.BlockTagArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.generated.Tags
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
	var invalidBlocksTreshold: Int = 0,
) : FeatureConfig()

@Serializable
data class GeodeBlocks(
	var filling: BlockStateProvider = simpleStateProvider(),
	var innerLayer: BlockStateProvider = simpleStateProvider(),
	var alternateInnerLayer: BlockStateProvider = simpleStateProvider(),
	var middleLayer: BlockStateProvider = simpleStateProvider(),
	var outerLayer: BlockStateProvider = simpleStateProvider(),
	var innerPlacements: List<BlockState> = emptyList(),
	var cannotReplace: BlockTagArgument = Tags.Blocks.FEATURES_CANNOT_REPLACE,
	var invalidBlocks: BlockTagArgument = Tags.Blocks.GEODE_INVALID_BLOCKS,
)

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

fun geode(
	block: Geode.() -> Unit = {},
) = Geode().apply(block)

fun Geode.blocks(block: GeodeBlocks.() -> Unit = {}) = blocks.apply(block)

fun Geode.layers(block: GeodeLayers.() -> Unit = {}) = layers.apply(block)

fun Geode.crack(block: GeodeCrack.() -> Unit = {}) = crack.apply(block)
