package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions.Surface
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.PlacedFeatureArgument
import io.github.ayfri.kore.serializers.InlinableList
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
	var replaceable: InlinableList<BlockOrTagArgument>,
	var groundState: BlockStateProvider = simpleStateProvider(),
	var vegetationFeature: PlacedFeatureArgument,
) : FeatureConfig()

fun ConfiguredFeatures.waterloggedVegetationPatch(
	fileName: String,
	surface: Surface,
	replaceable: InlinableList<BlockOrTagArgument>,
	vegetationFeature: PlacedFeatureArgument,
	block: WaterloggedVegetationPatch.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(
		fileName,
		WaterloggedVegetationPatch(surface, replaceable = replaceable, vegetationFeature = vegetationFeature).apply(block),
	)
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
