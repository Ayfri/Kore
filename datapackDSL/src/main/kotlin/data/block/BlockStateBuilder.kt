package data.block

import arguments.Argument
import generated.Blocks

data class BlockStateBuilder(var name: Argument.Block = Blocks.STONE) {
	var properties = emptyMap<String, String>()

	fun build() = BlockState(name, properties.ifEmpty { null })
}

fun blockState(block: BlockStateBuilder.() -> Unit) = BlockStateBuilder().apply(block).build()
fun blockState(name: Argument.Block, properties: Map<String, String>) = BlockState(name, properties)
fun blockState(name: Argument.Block, vararg properties: Pair<String, String>) = BlockState(name, properties.toMap())
fun blockState(name: Argument.Block, block: BlockStateBuilder.() -> Unit = {}) = BlockStateBuilder().apply(block).build()

fun BlockStateBuilder.properties(block: BlockStatePropertiesBuilder.() -> Unit) {
	properties = BlockStatePropertiesBuilder().apply(block).properties
}
