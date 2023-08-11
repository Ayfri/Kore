package features.advancements.types

import arguments.types.resources.BlockArgument
import features.advancements.states.State
import net.benwoodworth.knbt.NbtCompound
import kotlinx.serialization.Serializable

@Serializable
data class Block(
	var blocks: List<BlockArgument>? = null,
	var tag: String? = null,
	var nbt: NbtCompound? = null,
	var state: Map<String, State<*>>? = null,
)

fun block(block: BlockArgument, init: Block.() -> Unit = {}) = Block(blocks = listOf(block)).apply(init)
