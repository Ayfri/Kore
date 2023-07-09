package features.itemmodifiers.functions

import arguments.stringifiedNbt
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtTag

@Serializable
data class SetNbt(
	val nbt: String
) : ItemFunctionSurrogate {
	constructor(nbt: NbtTag) : this(stringifiedNbt(nbt))
}
