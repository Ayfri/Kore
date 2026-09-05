package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.arguments.components.ComponentsPatch
import io.github.ayfri.kore.arguments.components.matchers.DataComponentPredicate
import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.utils.nbt as buildNbt
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder

/**
 * Matches a block, its block state, its block entity NBT and its block entity components.
 *
 * Used as the `block` key of a [LocationPredicate], by the `can_place_on` and `can_break` item components, and by any
 * other place vanilla expects a block predicate.
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@Serializable
data class BlockPredicate(
	var blocks: InlinableList<BlockOrTagArgument>? = null,
	var components: ComponentsPatch? = null,
	var nbt: NbtCompound? = null,
	var predicates: DataComponentPredicate? = null,
	var state: Map<String, String>? = null,
)

/** Creates a [BlockPredicate] matching any of [blocks]. */
fun blockPredicate(vararg blocks: BlockOrTagArgument, init: BlockPredicate.() -> Unit = {}) =
	BlockPredicate(blocks = blocks.toList().ifEmpty { null }).apply(init)

/** Restricts this predicate to the given [blocks]. */
fun BlockPredicate.blocks(vararg blocks: BlockOrTagArgument) {
	this.blocks = blocks.toList()
}

/** Matches exact data component values on the block entity. */
fun BlockPredicate.components(block: ComponentsPatch.() -> Unit) {
	components = ComponentsPatch().apply(block)
}

/** Matches the block entity NBT. */
fun BlockPredicate.nbt(block: NbtCompoundBuilder.() -> Unit) {
	nbt = buildNbt(block)
}

/** Tests data component values on the block entity. */
fun BlockPredicate.predicates(block: DataComponentPredicate.() -> Unit) {
	predicates = DataComponentPredicate().apply(block)
}

/** Requires the block state property [key] to equal [value]. */
fun BlockPredicate.state(key: String, value: String) {
	state = mapOf(key to value)
}

/** Requires all the block state properties declared in [block]. */
fun BlockPredicate.states(block: MutableMap<String, String>.() -> Unit) {
	state = buildMap(block)
}
