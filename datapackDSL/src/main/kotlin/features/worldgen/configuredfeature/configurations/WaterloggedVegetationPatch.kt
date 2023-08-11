package features.worldgen.configuredfeature.configurations

import arguments.types.resources.tagged.BlockTagArgument
import arguments.types.resources.worldgen.PlacedFeatureArgument
import features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import features.worldgen.intproviders.IntProvider
import features.worldgen.intproviders.constant
import features.worldgen.noisesettings.rules.conditions.Surface
import kotlinx.serialization.Serializable

@Serializable
data class WaterloggedVegetationPatch(
	var surface: Surface,
	var depth: IntProvider = constant(0),
	var verticalRange: Int = 0,
	var extraBottomBlockChance: Double = 0.0,
	var extraEdgeColumnChance: Double = 0.0,
	var vegetationChance: Double = 0.0,
	var xzRadius: Int = 0,
	var replaceable: BlockTagArgument,
	var groundState: BlockStateProvider = simpleStateProvider(),
	var vegetationFeature: PlacedFeatureArgument,
) : FeatureConfig()

fun waterloggedVegetationPatch(
	surface: Surface,
	replaceable: BlockTagArgument,
	vegetationFeature: PlacedFeatureArgument,
	block: WaterloggedVegetationPatch.() -> Unit = {},
) = WaterloggedVegetationPatch(surface, replaceable = replaceable, vegetationFeature = vegetationFeature).apply(block)
