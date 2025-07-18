package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.arguments.types.resources.tagged.BlockTagArgument
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions.Surface
import io.github.ayfri.kore.generated.arguments.worldgen.types.PlacedFeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data class VegetationPatch(
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

fun vegetationPatch(
	surface: Surface,
	replaceable: BlockTagArgument,
	vegetationFeature: PlacedFeatureArgument,
	block: VegetationPatch.() -> Unit = {},
) = VegetationPatch(surface, replaceable = replaceable, vegetationFeature = vegetationFeature).apply(block)
