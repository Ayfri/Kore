package io.github.ayfri.kore.utils

import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.serializers.NbtAsJsonSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.benwoodworth.knbt.*

val snbtSerializer = StringifiedNbt {
	nameRootClasses = false
}

// region Builders

fun nbt(block: NbtCompoundBuilder.() -> Unit = {}) = buildNbtCompound(block)
fun <T : NbtTag> nbtList(block: NbtListBuilder<T>.() -> Unit = {}) = buildNbtList(block)

@JvmName("nbtListCompound")
fun nbtList(block: NbtListBuilder<NbtCompound>.() -> Unit = {}) = buildNbtList(block)

fun NbtCompoundBuilder.nbt(name: String, block: NbtCompoundBuilder.() -> Unit = {}) = putNbtCompound(name, block)
fun NbtCompoundBuilder.json(name: String, block: NbtCompoundBuilder.() -> Unit = {}) =
	put(name, StringifiedNbt.encodeToString(buildNbtCompound(block)))

fun stringifiedNbt(nbt: NbtTag) = StringifiedNbt.encodeToString(nbt)
fun stringifiedNbt(block: NbtCompoundBuilder.() -> Unit) = StringifiedNbt.encodeToString(buildNbtCompound(block))
fun stringifiedNbtList(block: NbtListBuilder<NbtCompound>.() -> Unit) =
	StringifiedNbt.encodeToString(buildNbtList(block))

fun nbt(nbt: NbtTag) = literal(StringifiedNbt.encodeToString(nbt))
fun nbtArg(nbt: NbtCompoundBuilder.() -> Unit) = literal(StringifiedNbt.encodeToString(buildNbtCompound(nbt)))
fun nbtText(nbt: NbtTag) = literal(Json.encodeToString(NbtAsJsonSerializer, nbt))

@JvmName("nbtNullable")
internal fun nbt(nbt: NbtTag? = null) = nbt?.let { nbt(it) }

// endregion

// region Set operator for NbtCompoundBuilder

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

inline operator fun <reified T : @Serializable Any> NbtCompoundBuilder.set(name: String, value: T) =
	put(name, Json.encodeToString(value))

// endregion

// region Plus operator for NbtListBuilder (returns new list)

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

// endregion

// region PlusAssign operator for NbtListBuilder (+=)

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

// endregion

// region AddAll for NbtListBuilder

@JvmName("addAllNbtBytes")
fun NbtListBuilder<NbtByte>.addAll(tags: Iterable<NbtByte>) = tags.forEach { add(it) }

@JvmName("addAllNbtByteArrays")
fun NbtListBuilder<NbtByteArray>.addAll(tags: Iterable<NbtByteArray>) = tags.forEach { add(it) }

@JvmName("addAllNbtCompounds")
fun NbtListBuilder<NbtCompound>.addAll(tags: Iterable<NbtCompound>) = tags.forEach { add(it) }

@JvmName("addAllNbtDoubles")
fun NbtListBuilder<NbtDouble>.addAll(tags: Iterable<NbtDouble>) = tags.forEach { add(it) }

@JvmName("addAllNbtFloats")
fun NbtListBuilder<NbtFloat>.addAll(tags: Iterable<NbtFloat>) = tags.forEach { add(it) }

@JvmName("addAllNbtInts")
fun NbtListBuilder<NbtInt>.addAll(tags: Iterable<NbtInt>) = tags.forEach { add(it) }

@JvmName("addAllNbtIntArrays")
fun NbtListBuilder<NbtIntArray>.addAll(tags: Iterable<NbtIntArray>) = tags.forEach { add(it) }

@JvmName("addAllNbtLists")
fun <T : NbtTag> NbtListBuilder<NbtList<T>>.addAll(tags: Iterable<NbtList<T>>) = tags.forEach { add(it) }

@JvmName("addAllNbtLongs")
fun NbtListBuilder<NbtLong>.addAll(tags: Iterable<NbtLong>) = tags.forEach { add(it) }

@JvmName("addAllNbtLongArrays")
fun NbtListBuilder<NbtLongArray>.addAll(tags: Iterable<NbtLongArray>) = tags.forEach { add(it) }

@JvmName("addAllNbtShorts")
fun NbtListBuilder<NbtShort>.addAll(tags: Iterable<NbtShort>) = tags.forEach { add(it) }

@JvmName("addAllNbtStrings")
fun NbtListBuilder<NbtString>.addAll(tags: Iterable<NbtString>) = tags.forEach { add(it) }

@JvmName("addAllBytes")
fun NbtListBuilder<NbtByte>.addAll(values: Iterable<Byte>) = values.forEach { add(NbtByte(it)) }

@JvmName("addAllShorts")
fun NbtListBuilder<NbtShort>.addAll(values: Iterable<Short>) = values.forEach { add(NbtShort(it)) }

@JvmName("addAllInts")
fun NbtListBuilder<NbtInt>.addAll(values: Iterable<Int>) = values.forEach { add(NbtInt(it)) }

@JvmName("addAllLongs")
fun NbtListBuilder<NbtLong>.addAll(values: Iterable<Long>) = values.forEach { add(NbtLong(it)) }

@JvmName("addAllFloats")
fun NbtListBuilder<NbtFloat>.addAll(values: Iterable<Float>) = values.forEach { add(NbtFloat(it)) }

@JvmName("addAllDoubles")
fun NbtListBuilder<NbtDouble>.addAll(values: Iterable<Double>) = values.forEach { add(NbtDouble(it)) }

@JvmName("addAllStringsFromIterable")
fun NbtListBuilder<NbtString>.addAll(values: Iterable<String>) = values.forEach { add(NbtString(it)) }

@JvmName("addAllSerializable")
inline fun <reified T : @Serializable Any> NbtListBuilder<NbtString>.addAll(values: Iterable<T>) =
	values.forEach { add(Json.encodeToString(it)) }

// endregion

// region String extension

val String.nbt get() = NbtString(this)

// endregion

// region NbtListOf factory functions

fun nbtListOf(vararg elements: Byte) = nbtList<NbtByte> { elements.forEach { add(it) } }
fun nbtListOf(vararg elements: Short) = nbtList<NbtShort> { elements.forEach { add(it) } }
fun nbtListOf(vararg elements: Int) = nbtList<NbtInt> { elements.forEach { add(it) } }
fun nbtListOf(vararg elements: Long) = nbtList<NbtLong> { elements.forEach { add(it) } }
fun nbtListOf(vararg elements: Float) = nbtList<NbtFloat> { elements.forEach { add(it) } }
fun nbtListOf(vararg elements: Double) = nbtList<NbtDouble> { elements.forEach { add(it) } }
fun nbtListOf(vararg elements: String) = nbtList<NbtString> { elements.forEach { add(it) } }
fun nbtListOf(vararg elements: ByteArray) = nbtList<NbtByteArray> { elements.forEach { add(it) } }
fun nbtListOf(vararg elements: IntArray) = nbtList<NbtIntArray> { elements.forEach { add(it) } }
fun nbtListOf(vararg elements: LongArray) = nbtList<NbtLongArray> { elements.forEach { add(it) } }
fun nbtListOf(vararg elements: NbtCompound) = nbtList<NbtCompound> { elements.forEach { add(it) } }

@JvmName("nbtListOfBytes")
fun nbtListOf(elements: Iterable<Byte>) = nbtList<NbtByte> { elements.forEach { add(it) } }

@JvmName("nbtListOfShorts")
fun nbtListOf(elements: Iterable<Short>) = nbtList<NbtShort> { elements.forEach { add(it) } }

@JvmName("nbtListOfInts")
fun nbtListOf(elements: Iterable<Int>) = nbtList<NbtInt> { elements.forEach { add(it) } }

@JvmName("nbtListOfLongs")
fun nbtListOf(elements: Iterable<Long>) = nbtList<NbtLong> { elements.forEach { add(it) } }

@JvmName("nbtListOfFloats")
fun nbtListOf(elements: Iterable<Float>) = nbtList<NbtFloat> { elements.forEach { add(it) } }

@JvmName("nbtListOfDoubles")
fun nbtListOf(elements: Iterable<Double>) = nbtList<NbtDouble> { elements.forEach { add(it) } }

@JvmName("nbtListOfStrings")
fun nbtListOf(elements: Iterable<String>) = nbtList<NbtString> { elements.forEach { add(it) } }

@JvmName("nbtListOfByteArrays")
fun nbtListOf(elements: Iterable<ByteArray>) = nbtList<NbtByteArray> { elements.forEach { add(it) } }

@JvmName("nbtListOfIntArrays")
fun nbtListOf(elements: Iterable<IntArray>) = nbtList<NbtIntArray> { elements.forEach { add(it) } }

@JvmName("nbtListOfLongArrays")
fun nbtListOf(elements: Iterable<LongArray>) = nbtList<NbtLongArray> { elements.forEach { add(it) } }

@JvmName("nbtListOfCompounds")
fun nbtListOf(elements: Iterable<NbtCompound>) = nbtList<NbtCompound> { elements.forEach { add(it) } }

// endregion
