package io.github.ayfri.kore.features.advancements.types

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.features.advancements.states.State
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
