package nbt

sealed class NbtEntry {
	abstract fun asString(): kotlin.String
	
	class Byte(val value: kotlin.Byte) : NbtEntry() {
		override fun asString() = "${value}B"
	}
	
	class Short(val value: kotlin.Short) : NbtEntry() {
		override fun asString() = "${value}S"
	}
	
	class Int(val value: kotlin.Int) : NbtEntry() {
		override fun asString() = value.toString()
	}
	
	class Long(val value: kotlin.Long) : NbtEntry() {
		override fun asString() = "${value}L"
	}
	
	class Float(val value: kotlin.Float) : NbtEntry() {
		override fun asString() = "${value}F"
	}
	
	class Double(val value: kotlin.Double) : NbtEntry() {
		override fun asString() = value.toString()
	}
	
	class ByteArray(val value: kotlin.ByteArray) : NbtEntry() {
		override fun asString() = value.joinToString(prefix = "[B;", postfix = "]") { it.toString() }
	}
	
	class String(val value: kotlin.String) : NbtEntry() {
		override fun asString() = "\"$value\""
	}
	
	class List(val value: kotlin.collections.List<NbtEntry>) : NbtEntry() {
		override fun asString() = value.joinToString(prefix = "[", postfix = "]")
	}
	
	class Compound(val value: NbtData) : NbtEntry() {
		override fun asString() = value.toString()
	}

	class IntArray(val value: kotlin.IntArray) : NbtEntry() {
		override fun asString() = value.joinToString(prefix = "[I;", postfix = "]")
	}

	class LongArray(val value: kotlin.LongArray) : NbtEntry() {
		override fun asString() = value.joinToString(prefix = "[L;", postfix = "]")
	}
}

class NbtData(val entries: MutableMap<String, NbtEntry> = mutableMapOf()) {
	override fun toString() = entries.map { "${it.key}:${it.value.asString()}" }.joinToString(prefix = "{", postfix = "}")
}

fun nbtData(block: NbtData.() -> Unit) = NbtData().apply(block)

fun NbtData.byte(name: String, value: Byte) = NbtEntry.Byte(value).also { entries[name] = it }
fun NbtData.short(name: String, value: Short) = NbtEntry.Short(value).also { entries[name] = it }
fun NbtData.int(name: String, value: Int) = NbtEntry.Int(value).also { entries[name] = it }
fun NbtData.long(name: String, value: Long) = NbtEntry.Long(value).also { entries[name] = it }
fun NbtData.float(name: String, value: Float) = NbtEntry.Float(value).also { entries[name] = it }
fun NbtData.double(name: String, value: Double) = NbtEntry.Double(value).also { entries[name] = it }
fun NbtData.byteArray(name: String, value: ByteArray) = NbtEntry.ByteArray(value).also { entries[name] = it }
fun NbtData.string(name: String, value: String) = NbtEntry.String(value).also { entries[name] = it }
fun NbtData.list(name: String, value: List<NbtEntry>) = NbtEntry.List(value).also { entries[name] = it }
fun NbtData.compound(name: String, value: NbtData) = NbtEntry.Compound(value).also { entries[name] = it }
fun NbtData.intArray(name: String, value: IntArray) = NbtEntry.IntArray(value).also { entries[name] = it }
fun NbtData.longArray(name: String, value: LongArray) = NbtEntry.LongArray(value).also { entries[name] = it }

fun NbtData.nbt(name: String, block: NbtData.() -> Unit) = compound(name, nbtData(block))
fun NbtData.json(name: String, block: NbtData.() -> Unit) = string(name, nbtData(block).toString())
fun NbtData.bool(name: String, value: Boolean) = byte(name, if (value) 1 else 0)
