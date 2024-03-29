package io.github.ayfri.kore.features.advancements.types

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.features.advancements.states.State
import io.github.ayfri.kore.serializers.InlinableList
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.buildNbtCompound
import kotlinx.serialization.Serializable

@Serializable
data class Block(
	var blocks: InlinableList<BlockOrTagArgument>? = null,
	var nbt: NbtCompound? = null,
	var state: Map<String, State<*>>? = null,
)

fun block(block: BlockOrTagArgument, init: Block.() -> Unit = {}) = Block(blocks = listOf(block)).apply(init)

fun blocks(vararg blocks: BlockOrTagArgument, init: Block.() -> Unit = {}) = Block(blocks = blocks.toList()).apply(init)

fun Block.blocks(vararg blocks: BlockOrTagArgument) {
	this.blocks = blocks.toList()
}

fun Block.nbt(block: NbtCompoundBuilder.() -> Unit) {
	nbt = buildNbtCompound(block)
}

fun Block.state(key: String, value: State<*>) {
	state = mapOf(key to value)
}

fun Block.states(block: MutableMap<String, State<*>>.() -> Unit) {
	state = buildMap(block)
}
