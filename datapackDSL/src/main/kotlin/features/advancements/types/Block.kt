package features.advancements.types

import arguments.types.resources.BlockArgument
import features.advancements.states.State
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompound

@Serializable
data class Block(
	var blocks: List<BlockArgument>? = null,
	var tag: String? = null,
	var nbt: NbtCompound? = null,
	var state: Map<String, State<*>>? = null,
)

fun block(block: BlockArgument, init: Block.() -> Unit = {}): Block {
	val block = Block(blocks = listOf(block))
	block.init()
	return block
}
