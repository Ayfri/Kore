package features.itemmodifiers.functions

import arguments.nbt
import arguments.stringifiedNbt
import features.itemmodifiers.ItemModifier
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.NbtTag

@Serializable
data class SetNbt(
	val nbt: String
) : ItemFunction() {
	constructor(nbt: NbtTag) : this(stringifiedNbt(nbt))
}

fun ItemModifier.setNbt(nbt: NbtTag, block: SetNbt.() -> Unit = {}) {
	modifiers += SetNbt(nbt).apply(block)
}

fun ItemModifier.setNbt(block: NbtCompoundBuilder.() -> Unit) =
	SetNbt(nbt(block)).also { modifiers += it }
