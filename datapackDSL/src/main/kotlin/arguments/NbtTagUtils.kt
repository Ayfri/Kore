package arguments

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.benwoodworth.knbt.*
import serializers.NbtAsJsonTextComponentSerializer

fun nbt(block: NbtCompoundBuilder.() -> Unit = {}) = buildNbtCompound(block)
fun <T : NbtTag> nbtList(block: NbtListBuilder<T>.() -> Unit = {}) = buildNbtList(block)

@JvmName("nbtListCompound")
fun nbtList(block: NbtListBuilder<NbtCompound>.() -> Unit = {}) = buildNbtList(block)

fun NbtCompoundBuilder.nbt(name: String, block: NbtCompoundBuilder.() -> Unit = {}) = putNbtCompound(name, block)
fun NbtCompoundBuilder.json(name: String, block: NbtCompoundBuilder.() -> Unit = {}) = put(name, StringifiedNbt.encodeToString(buildNbtCompound(block)))

fun stringifiedNbt(block: NbtCompoundBuilder.() -> Unit) = StringifiedNbt.encodeToString(buildNbtCompound(block))
fun stringifiedNbtList(block: NbtListBuilder<NbtCompound>.() -> Unit) = StringifiedNbt.encodeToString(buildNbtList(block))

fun nbt(nbt: NbtTag) = literal(StringifiedNbt.encodeToString(nbt))
fun nbtText(nbt: NbtTag) = literal(Json.encodeToString(NbtAsJsonTextComponentSerializer, nbt))

@JvmName("nbtNullable")
internal fun nbt(nbt: NbtTag? = null) = nbt?.let { nbt(it) }

operator fun NbtCompoundBuilder.set(name: String, value: NbtTag) = put(name, value)
operator fun NbtCompoundBuilder.set(name: String, value: String) = put(name, value)
operator fun NbtCompoundBuilder.set(name: String, value: Boolean) = put(name, value)
operator fun NbtCompoundBuilder.set(name: String, value: Byte) = put(name, value)
operator fun NbtCompoundBuilder.set(name: String, value: Short) = put(name, value)
operator fun NbtCompoundBuilder.set(name: String, value: Int) = put(name, value)
operator fun NbtCompoundBuilder.set(name: String, value: Long) = put(name, value)
operator fun NbtCompoundBuilder.set(name: String, value: Float) = put(name, value)
operator fun NbtCompoundBuilder.set(name: String, value: Double) = put(name, value)
operator fun NbtCompoundBuilder.set(name: String, value: ByteArray) = put(name, value)
operator fun NbtCompoundBuilder.set(name: String, value: IntArray) = put(name, value)
operator fun NbtCompoundBuilder.set(name: String, value: LongArray) = put(name, value)

inline operator fun <reified T : @Serializable Any> NbtCompoundBuilder.set(name: String, value: T) = put(name, Json.encodeToString(value))

operator fun NbtListBuilder<NbtByte>.plus(tag: NbtByte) {
	add(tag)
}

operator fun NbtListBuilder<NbtByteArray>.plus(tag: NbtByteArray) {
	add(tag)
}

operator fun NbtListBuilder<NbtCompound>.plus(tag: NbtCompound) {
	add(tag)
}

operator fun NbtListBuilder<NbtDouble>.plus(tag: NbtDouble) {
	add(tag)
}

operator fun NbtListBuilder<NbtFloat>.plus(tag: NbtFloat) {
	add(tag)
}

operator fun NbtListBuilder<NbtInt>.plus(tag: NbtInt) {
	add(tag)
}

operator fun NbtListBuilder<NbtIntArray>.plus(tag: NbtIntArray) {
	add(tag)
}

operator fun <T : NbtTag> NbtListBuilder<NbtList<T>>.plus(tag: NbtList<T>) {
	add(tag)
}

operator fun NbtListBuilder<NbtLong>.plus(tag: NbtLong) {
	add(tag)
}

operator fun NbtListBuilder<NbtLongArray>.plus(tag: NbtLongArray) {
	add(tag)
}

operator fun NbtListBuilder<NbtShort>.plus(tag: NbtShort) {
	add(tag)
}

operator fun NbtListBuilder<NbtString>.plus(tag: NbtString) {
	add(tag)
}

operator fun NbtListBuilder<NbtByte>.plus(value: Byte) {
	add(NbtByte(value))
}

operator fun NbtListBuilder<NbtByte>.plus(value: Boolean) {
	add(NbtByte(value))
}

operator fun NbtListBuilder<NbtShort>.plus(value: Short) {
	add(NbtShort(value))
}

operator fun NbtListBuilder<NbtInt>.plus(value: Int) {
	add(NbtInt(value))
}

operator fun NbtListBuilder<NbtLong>.plus(value: Long) {
	add(NbtLong(value))
}

operator fun NbtListBuilder<NbtFloat>.plus(value: Float) {
	add(NbtFloat(value))
}

operator fun NbtListBuilder<NbtDouble>.plus(value: Double) {
	add(NbtDouble(value))
}

operator fun NbtListBuilder<NbtByteArray>.plus(value: ByteArray) {
	add(NbtByteArray(value))
}

operator fun NbtListBuilder<NbtString>.plus(value: String) {
	add(NbtString(value))
}

operator fun NbtListBuilder<NbtIntArray>.plus(value: IntArray) {
	add(NbtIntArray(value))
}

operator fun NbtListBuilder<NbtLongArray>.plus(value: LongArray) {
	add(NbtLongArray(value))
}

inline operator fun <reified T : @Serializable Any> NbtListBuilder<NbtString>.plus(value: T) {
	add(Json.encodeToString(value))
}


operator fun NbtListBuilder<NbtByte>.plusAssign(tag: NbtByte) {
	add(tag)
}

operator fun NbtListBuilder<NbtByteArray>.plusAssign(tag: NbtByteArray) {
	add(tag)
}

operator fun NbtListBuilder<NbtCompound>.plusAssign(tag: NbtCompound) {
	add(tag)
}

operator fun NbtListBuilder<NbtDouble>.plusAssign(tag: NbtDouble) {
	add(tag)
}

operator fun NbtListBuilder<NbtFloat>.plusAssign(tag: NbtFloat) {
	add(tag)
}

operator fun NbtListBuilder<NbtInt>.plusAssign(tag: NbtInt) {
	add(tag)
}

operator fun NbtListBuilder<NbtIntArray>.plusAssign(tag: NbtIntArray) {
	add(tag)
}

operator fun <T : NbtTag> NbtListBuilder<NbtList<T>>.plusAssign(tag: NbtList<T>) {
	add(tag)
}

operator fun NbtListBuilder<NbtLong>.plusAssign(tag: NbtLong) {
	add(tag)
}

operator fun NbtListBuilder<NbtLongArray>.plusAssign(tag: NbtLongArray) {
	add(tag)
}

operator fun NbtListBuilder<NbtShort>.plusAssign(tag: NbtShort) {
	add(tag)
}

operator fun NbtListBuilder<NbtString>.plusAssign(tag: NbtString) {
	add(tag)
}


operator fun NbtListBuilder<NbtByte>.plusAssign(value: Byte) {
	add(NbtByte(value))
}

operator fun NbtListBuilder<NbtShort>.plusAssign(value: Short) {
	add(NbtShort(value))
}

operator fun NbtListBuilder<NbtInt>.plusAssign(value: Int) {
	add(NbtInt(value))
}

operator fun NbtListBuilder<NbtLong>.plusAssign(value: Long) {
	add(NbtLong(value))
}

operator fun NbtListBuilder<NbtFloat>.plusAssign(value: Float) {
	add(NbtFloat(value))
}

operator fun NbtListBuilder<NbtDouble>.plusAssign(value: Double) {
	add(NbtDouble(value))
}

operator fun NbtListBuilder<NbtByteArray>.plusAssign(value: ByteArray) {
	add(NbtByteArray(value))
}

operator fun NbtListBuilder<NbtString>.plusAssign(value: String) {
	add(NbtString(value))
}

operator fun NbtListBuilder<NbtIntArray>.plusAssign(value: IntArray) {
	add(NbtIntArray(value))
}

operator fun NbtListBuilder<NbtLongArray>.plusAssign(value: LongArray) {
	add(NbtLongArray(value))
}

inline operator fun <reified T : @Serializable Any> NbtListBuilder<NbtString>.plusAssign(value: T) {
	add(Json.encodeToString(value))
}

val String.nbt get() = NbtString(this)


fun nbtListOf(vararg elements: Byte) = nbtList<NbtByte> {
	elements.forEach { add(it) }
}

fun nbtListOf(vararg elements: Short) = nbtList<NbtShort> {
	elements.forEach { add(it) }
}

fun nbtListOf(vararg elements: Int) = nbtList<NbtInt> {
	elements.forEach { add(it) }
}

fun nbtListOf(vararg elements: Long) = nbtList<NbtLong> {
	elements.forEach { add(it) }
}

fun nbtListOf(vararg elements: Float) = nbtList<NbtFloat> {
	elements.forEach { add(it) }
}

fun nbtListOf(vararg elements: Double) = nbtList<NbtDouble> {
	elements.forEach { add(it) }
}

fun nbtListOf(vararg elements: String) = nbtList<NbtString> {
	elements.forEach { add(it) }
}

fun nbtListOf(vararg elements: ByteArray) = nbtList<NbtByteArray> {
	elements.forEach { add(it) }
}

fun nbtListOf(vararg elements: IntArray) = nbtList<NbtIntArray> {
	elements.forEach { add(it) }
}

fun nbtListOf(vararg elements: LongArray) = nbtList<NbtLongArray> {
	elements.forEach { add(it) }
}
