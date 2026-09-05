package io.github.ayfri.kore.data.block

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.generated.Blocks

data class BlockStateBuilder(var name: BlockArgument = Blocks.STONE) {
	var properties = emptyMap<String, String>()

	fun build() = BlockState(name, properties.ifEmpty { null })
}

fun blockState(block: BlockStateBuilder.() -> Unit) = BlockStateBuilder().apply(block).build()
fun blockState(name: BlockArgument, properties: Map<String, String>) = BlockState(name, properties)
fun blockState(name: BlockArgument, vararg properties: Pair<String, String>) = BlockState(name, properties.toMap())
fun blockState(name: BlockArgument, block: BlockStateBuilder.() -> Unit = {}) = BlockStateBuilder(name).apply(block).build()

/** Creates a [BlockState] with the [Blocks.STONE] block and a lambda to edit it. */
fun blockStateStone(block: BlockStateBuilder.() -> Unit = {}) = blockState(Blocks.STONE, block)

fun BlockStateBuilder.properties(block: BlockStatePropertiesBuilder.() -> Unit) {
	properties = BlockStatePropertiesBuilder().apply(block).properties
}
