package nbt

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtInt
import net.benwoodworth.knbt.NbtString
import net.benwoodworth.knbt.NbtTag

class MutableNbtCompound(
	private val nbtData: MutableMap<String, NbtTag> = mutableMapOf(),
) : MutableMap<String, NbtTag> by nbtData {
	override fun toString(): String {
		return nbtData.toString()
	}

	fun toNbtCompound() = NbtCompound(nbtData.toMap())
	fun toMutableNbtCompound() = MutableNbtCompound(nbtData.toMutableMap())

	fun nbtCompound(key: String, value: MutableNbtCompound.() -> Unit) {
		nbtData[key] = MutableNbtCompound().apply(value).toNbtCompound()
	}

	operator fun set(key: String, value: NbtTag) {
		nbtData[key] = value
	}

	operator fun set(key: String, value: String) {
		nbtData[key] = NbtString(value)
	}

	operator fun set(key: String, value: Boolean) {
		nbtData[key] = NbtInt(if (value) 1 else 0)
	}

	operator fun set(key: String, value: Byte) {
		nbtData[key] = NbtInt(value.toInt())
	}

	operator fun set(key: String, value: Short) {
		nbtData[key] = NbtInt(value.toInt())
	}

	operator fun set(key: String, value: Int) {
		nbtData[key] = NbtInt(value)
	}

	operator fun set(key: String, value: Long) {
		nbtData[key] = NbtInt(value.toInt())
	}

	operator fun set(key: String, value: Float) {
		nbtData[key] = NbtInt(value.toInt())
	}

	operator fun set(key: String, value: Double) {
		nbtData[key] = NbtInt(value.toInt())
	}

	operator fun set(key: String, value: ByteArray) {
		nbtData[key] = NbtInt(value.size)
	}

	operator fun set(key: String, value: IntArray) {
		nbtData[key] = NbtInt(value.size)
	}

	operator fun set(key: String, value: LongArray) {
		nbtData[key] = NbtInt(value.size)
	}

	operator fun set(key: String, value: MutableNbtCompound) {
		nbtData[key] = value.toNbtCompound()
	}

	inline operator fun <reified T : @Serializable Any> MutableNbtCompound.set(name: String, value: T) {
		this[name] = Json.encodeToString(value)
	}
}

fun mutableNbt(value: MutableNbtCompound.() -> Unit = {}) = MutableNbtCompound().apply(value)
