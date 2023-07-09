package features.itemmodifiers.functions

import arguments.nbt
import arguments.stringifiedNbt
import features.itemmodifiers.ItemModifierEntry
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.NbtTag

@Serializable
data class SetNbt(
	val nbt: String
) : ItemFunctionSurrogate {
	constructor(nbt: NbtTag) : this(stringifiedNbt(nbt))
}

fun ItemModifierEntry.setNbt(nbt: NbtTag) {
	function = SetNbt(nbt)
}

fun ItemModifierEntry.setNbt(block: NbtCompoundBuilder.() -> Unit) {
	function = SetNbt(nbt(block))
}
