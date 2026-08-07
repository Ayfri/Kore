package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.Direction
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data class BlockColumn(
	var direction: Direction,
	var allowedPlacement: BlockPredicate = True,
	var prioritizeTip: Boolean = false,
	var layers: List<Layer> = emptyList(),
) : FeatureConfig()

@Serializable
data class Layer(
	var height: IntProvider = constant(0),
	var provider: BlockStateProvider = simpleStateProvider(),
)

fun BlockColumn.layer(height: IntProvider = constant(0), provider: BlockStateProvider = simpleStateProvider()) {
	layers += Layer(height, provider)
}

fun BlockColumn.layers(list: List<Layer>) = run { layers = list }
fun BlockColumn.layers(vararg layers: Layer) = run { this.layers = layers.toList() }
fun BlockColumn.layers(block: MutableList<Layer>.() -> Unit) = run { layers = buildList(block) }

fun MutableList<Layer>.layer(height: IntProvider = constant(0), provider: BlockStateProvider = simpleStateProvider()) {
	this += Layer(height, provider)
}

fun ConfiguredFeatures.blockColumn(
	fileName: String,
	direction: Direction,
	allowedPlacement: BlockPredicate = True,
	prioritizeTip: Boolean = false,
	layers: List<Layer> = emptyList(),
	block: BlockColumn.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, BlockColumn(direction, allowedPlacement, prioritizeTip, layers).apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
