package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
import io.github.ayfri.kore.features.worldgen.configuredfeature.Direction
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
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

fun blockColumn(
	direction: Direction,
	allowedPlacement: BlockPredicate = True,
	prioritizeTip: Boolean = false,
	layers: List<Layer> = emptyList(),
	block: BlockColumn.() -> Unit = {},
) = BlockColumn(direction, allowedPlacement, prioritizeTip, layers).apply(block)

fun blockColumn(
	direction: Direction,
	allowedPlacement: BlockPredicate = True,
	prioritizeTip: Boolean = false,
	vararg layers: Layer,
) = BlockColumn(direction, allowedPlacement, prioritizeTip, layers.toList())

fun BlockColumn.layer(height: IntProvider = constant(0), provider: BlockStateProvider = simpleStateProvider()) {
	layers += Layer(height, provider)
}

fun BlockColumn.layers(list: List<Layer>) = run { layers = list }
fun BlockColumn.layers(vararg layers: Layer) = run { this.layers = layers.toList() }
fun BlockColumn.layers(block: MutableList<Layer>.() -> Unit) = run { layers = buildList(block) }

fun MutableList<Layer>.layer(height: IntProvider = constant(0), provider: BlockStateProvider = simpleStateProvider()) {
	this += Layer(height, provider)
}
