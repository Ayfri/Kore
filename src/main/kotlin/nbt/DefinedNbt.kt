package nbt

class DefinedNbt<T : Enum<T>>(val enum: Enum<T>, val value: NbtEntry) {
	override fun toString() = "${enum.name.lowercase()}=${value.asString()}"
}
