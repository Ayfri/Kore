package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.BlockStateBuilder
import io.github.ayfri.kore.data.block.blockStateStone
import kotlinx.serialization.Serializable

@Serializable
data class SimpleStateProvider(
	var state: BlockState = blockStateStone(),
) : BlockStateProvider()

fun simpleStateProvider(state: BlockState = blockStateStone()) = SimpleStateProvider(state)
fun simpleStateProvider(name: BlockArgument, properties: Map<String, String>) = SimpleStateProvider(BlockState(name, properties))
fun simpleStateProvider(name: BlockArgument, block: BlockStateBuilder.() -> Unit = {}) =
	SimpleStateProvider(BlockStateBuilder(name).apply(block).build())
