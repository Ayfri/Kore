package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class Speleothem(
	var chanceOfTallerGeneration: Double? = null,
	var chanceOfDirectionalSpread: Double? = null,
	var chanceOfSpreadRadius2: Double? = null,
	var chanceOfSpreadRadius3: Double? = null,
	var baseBlock: BlockState = blockStateStone(),
	var pointedBlock: BlockState = blockStateStone(),
	var replaceableBlocks: InlinableList<BlockOrTagArgument> = emptyList(),
) : FeatureConfig()

fun speleothem(
	chanceOfTallerGeneration: Double? = null,
	chanceOfDirectionalSpread: Double? = null,
	chanceOfSpreadRadius2: Double? = null,
	chanceOfSpreadRadius3: Double? = null,
	block: Speleothem.() -> Unit = {},
) = Speleothem(chanceOfTallerGeneration, chanceOfDirectionalSpread, chanceOfSpreadRadius2, chanceOfSpreadRadius3).apply(block)
