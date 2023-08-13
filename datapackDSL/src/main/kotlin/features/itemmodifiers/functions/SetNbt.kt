package features.itemmodifiers.functions

import features.itemmodifiers.ItemModifier
import features.predicates.PredicateAsList
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.NbtTag
import utils.nbt
import utils.stringifiedNbt
import kotlinx.serialization.Serializable

@Serializable
data class SetNbt(
	override var conditions: PredicateAsList? = null,
	val nbt: String,
) : ItemFunction() {
	constructor(nbt: NbtTag) : this(nbt = stringifiedNbt(nbt))
}

fun ItemModifier.setNbt(nbt: NbtTag, block: SetNbt.() -> Unit = {}) {
	modifiers += SetNbt(nbt).apply(block)
}

fun ItemModifier.setNbt(block: NbtCompoundBuilder.() -> Unit) =
	SetNbt(nbt(block)).also { modifiers += it }
