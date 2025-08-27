package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.arguments.components.ComponentsPatch
import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.features.predicates.sub.item.ItemStackSubPredicates
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.buildNbtCompound

@Serializable
data class Block(
	var blocks: InlinableList<BlockOrTagArgument>? = null,
	var components: ComponentsPatch? = null,
	var nbt: NbtCompound? = null,
	var predicates: ItemStackSubPredicates? = null,
	var state: Map<String, String>? = null,
)

fun block(block: BlockOrTagArgument, init: Block.() -> Unit = {}) = Block(blocks = listOf(block)).apply(init)

fun blocks(vararg blocks: BlockOrTagArgument, init: Block.() -> Unit = {}) = Block(blocks = blocks.toList()).apply(init)

fun Block.blocks(vararg blocks: BlockOrTagArgument) {
	this.blocks = blocks.toList()
}

fun Block.components(block: ComponentsPatch.() -> Unit) {
	components = ComponentsPatch().apply(block)
}

fun Block.nbt(block: NbtCompoundBuilder.() -> Unit) {
	nbt = buildNbtCompound(block)
}

fun Block.predicates(block: ItemStackSubPredicates.() -> Unit) {
	predicates = ItemStackSubPredicates().apply(block)
}

fun Block.state(key: String, value: String) {
	state = mapOf(key to value)
}

fun Block.states(block: MutableMap<String, String>.() -> Unit) {
	state = buildMap(block)
}
