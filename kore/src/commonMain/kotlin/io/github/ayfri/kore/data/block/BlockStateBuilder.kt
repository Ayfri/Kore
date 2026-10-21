package io.github.ayfri.kore.data.block

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.generated.Blocks

data class BlockStateBuilder(var id: BlockArgument = Blocks.STONE) {
	var properties = emptyMap<String, String>()

	fun build() = BlockState(id, properties.ifEmpty { null })
}

fun blockState(block: BlockStateBuilder.() -> Unit) = BlockStateBuilder().apply(block).build()
fun blockState(id: BlockArgument, properties: Map<String, String>) = BlockState(id, properties)
fun blockState(id: BlockArgument, vararg properties: Pair<String, String>) = BlockState(id, properties.toMap())
fun blockState(id: BlockArgument, block: BlockStateBuilder.() -> Unit = {}) = BlockStateBuilder(id).apply(block).build()

/** Creates a [BlockState] with the [Blocks.STONE] block and a lambda to edit it. */
fun blockStateStone(block: BlockStateBuilder.() -> Unit = {}) = blockState(Blocks.STONE, block)

fun BlockStateBuilder.properties(block: BlockStatePropertiesBuilder.() -> Unit) {
	properties = BlockStatePropertiesBuilder().apply(block).properties
}
