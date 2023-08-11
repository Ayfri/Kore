package features.worldgen.configuredfeature.blockstateprovider

import arguments.types.resources.BlockArgument
import data.block.BlockState
import data.block.BlockStateBuilder
import data.block.blockStateStone
import kotlinx.serialization.Serializable

@Serializable
data class SimpleStateProvider(
	var state: BlockState = blockStateStone(),
) : BlockStateProvider()

fun simpleStateProvider(state: BlockState = blockStateStone()) = SimpleStateProvider(state)
fun simpleStateProvider(name: BlockArgument, properties: Map<String, String>) = SimpleStateProvider(BlockState(name, properties))
fun simpleStateProvider(name: BlockArgument, block: BlockStateBuilder.() -> Unit = {}) =
	SimpleStateProvider(BlockStateBuilder(name).apply(block).build())
