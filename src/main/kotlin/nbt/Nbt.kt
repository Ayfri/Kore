package nbt

sealed class NbtEntry {
	abstract fun asString(): kotlin.String
	
	class Byte(val value: kotlin.Byte) : NbtEntry() {
		override fun asString() = value.toString()
	}
	class Short(val value: kotlin.Short) : NbtEntry() {
		override fun asString() = value.toString()
	}
	class Int(val value: kotlin.Int) : NbtEntry() {
		override fun asString() = value.toString()
	}
	class Long(val value: kotlin.Long) : NbtEntry() {
		override fun asString() = value.toString()
	}
	class Float(val value: kotlin.Float) : NbtEntry() {
		override fun asString() = value.toString()
	}
	class Double(val value: kotlin.Double) : NbtEntry() {
		override fun asString() = value.toString()
	}
	class ByteArray(val value: kotlin.ByteArray) : NbtEntry() {
		override fun asString() = value.toString()
	}
	class String(val value: kotlin.String) : NbtEntry() {
		override fun asString() = value
	}
	class List(val value: kotlin.collections.List<NbtEntry>) : NbtEntry() {
		override fun asString() = value.joinToString(", ", "[", "]")
	}
	class Compound(val value: kotlin.collections.List<Nbt<NbtEntry>>) : NbtEntry() {
		override fun asString() = value.joinToString(", ", "{", "}")
	}
	class IntArray(val value: kotlin.IntArray) : NbtEntry() {
		override fun asString() = value.toString()
	}
	class LongArray(val value: kotlin.LongArray) : NbtEntry() {
		override fun asString() = value.toString()
	}
}

fun byte(name: String, value: Byte) = Nbt(name, NbtEntry.Byte(value))
fun short(name: String, value: Short) = Nbt(name, NbtEntry.Short(value))
fun int(name: String, value: Int) = Nbt(name, NbtEntry.Int(value))
fun long(name: String, value: Long) = Nbt(name, NbtEntry.Long(value))
fun float(name: String, value: Float) = Nbt(name, NbtEntry.Float(value))
fun double(name: String, value: Double) = Nbt(name, NbtEntry.Double(value))
fun byteArray(name: String, value: ByteArray) = Nbt(name, NbtEntry.ByteArray(value))
fun string(name: String, value: String) = Nbt(name, NbtEntry.String(value))
fun list(name: String, value: List<NbtEntry>) = Nbt(name, NbtEntry.List(value))
fun compound(name: String, value: List<Nbt<NbtEntry>>) = Nbt(name, NbtEntry.Compound(value))
fun intArray(name: String, value: IntArray) = Nbt(name, NbtEntry.IntArray(value))
fun longArray(name: String, value: LongArray) = Nbt(name, NbtEntry.LongArray(value))
fun <T : NbtEntry> nbt(name: String, value: T) = Nbt(name, value)

class Nbt<T : NbtEntry>(val name: String, val value: T) {
	override fun toString() = "$name:${value.asString()}"
}

class NbtData(val nbt: Nbt<NbtEntry> = Nbt("", NbtEntry.Compound(mutableListOf()))) {
	override fun toString() = nbt.toString()
}

fun nbtData(nbt: Nbt<NbtEntry> = Nbt("", NbtEntry.Compound(mutableListOf()))) = NbtData(nbt)
