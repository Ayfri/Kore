package io.github.ayfri.kore.utils

import io.github.ayfri.kore.serializers.DataPackShapedSerializer
import io.github.ayfri.kore.serializers.snakeCased
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.serializer
import net.benwoodworth.knbt.*
import kotlin.jvm.JvmName

/** The SNBT format of Kore, classes encode as bare compounds instead of knbt's default `{"<serial name>":{...}}` root. */
val snbtSerializer = StringifiedNbt {
	nameRootClasses = false
}

// region Builders

fun nbt(block: NbtCompoundBuilder.() -> Unit = {}) = buildNbtCompound(block)
fun <T : NbtTag> nbtList(block: NbtListBuilder<T>.() -> Unit = {}) = buildNbtList(block)

@JvmName("nbtListCompound")
fun nbtList(block: NbtListBuilder<NbtCompound>.() -> Unit = {}) = buildNbtList(block)

fun nbtListOf(vararg elements: Byte) = NbtList(elements.map(::NbtByte))
fun nbtListOf(vararg elements: Short) = NbtList(elements.map(::NbtShort))
fun nbtListOf(vararg elements: Int) = NbtList(elements.map(::NbtInt))
fun nbtListOf(vararg elements: Long) = NbtList(elements.map(::NbtLong))
fun nbtListOf(vararg elements: Float) = NbtList(elements.map(::NbtFloat))
fun nbtListOf(vararg elements: Double) = NbtList(elements.map(::NbtDouble))
fun nbtListOf(vararg elements: String) = NbtList(elements.map(::NbtString))
fun nbtListOf(vararg elements: ByteArray) = NbtList(elements.map(::NbtByteArray))
fun nbtListOf(vararg elements: IntArray) = NbtList(elements.map(::NbtIntArray))
fun nbtListOf(vararg elements: LongArray) = NbtList(elements.map(::NbtLongArray))
fun nbtListOf(vararg elements: NbtCompound) = NbtList(elements.asList())

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

// endregion

// region SNBT

/**
 * SNBT form of this tag, like knbt's `toString()` but escaping backslashes and line breaks in strings too, which knbt
 * writes raw so the game reads `a\b` as an escape: `NbtString("a\\b").toSnbt()` is `"a\\b"`.
 *
 * A string value is written bare when it starts with a letter or `_` and only holds `[A-Za-z0-9_-]`, and is not
 * `true` or `false` in any case: `{color:aqua}`. Any other value stays quoted (`"minecraft:stone"`, `"@s"`, `"5"`, `"true"`).
 */
fun NbtTag.toSnbt(): String = buildString { appendSnbt(this@toSnbt) }

/** Encodes [value] with [snbtSerializer], writing the element names of classes in snake_case like the data pack JSON: `maxDamage` -> `max_damage`. */
inline fun <reified T> encodeToNbtTag(value: T) = encodeToNbtTag(serializer<T>(), value)
fun <T> encodeToNbtTag(serializer: SerializationStrategy<T>, value: T) = snbtSerializer.encodeToNbtTag(serializer.snakeCased(), value)

/** Encodes [value] to SNBT, see [encodeToNbtTag] and [toSnbt]. */
inline fun <reified T> encodeToSnbt(value: T) = encodeToNbtTag(value).toSnbt()
fun <T> encodeToSnbt(serializer: SerializationStrategy<T>, value: T) = encodeToNbtTag(serializer, value).toSnbt()

/** Decodes [tag] with [snbtSerializer], reading the element names of classes in snake_case, the inverse of [encodeToNbtTag]. */
inline fun <reified T> decodeFromNbtTag(tag: NbtTag) = decodeFromNbtTag(serializer<T>(), tag)
fun <T> decodeFromNbtTag(deserializer: DeserializationStrategy<T>, tag: NbtTag) = snbtSerializer.decodeFromNbtTag(deserializer.snakeCased(), tag)

/** Decodes SNBT to a [T], the inverse of [encodeToSnbt]: `decodeFromSnbt<Inner>("{inner_value:1}")`. */
inline fun <reified T> decodeFromSnbt(snbt: String) = decodeFromSnbt(serializer<T>(), snbt)
fun <T> decodeFromSnbt(deserializer: DeserializationStrategy<T>, snbt: String) = snbtSerializer.decodeFromString(deserializer.snakeCased(), snbt)

/** Encodes a data pack resource to SNBT like its data pack file (snake_case keys, defaults), see [DataPackShapedSerializer]. */
inline fun <reified T> encodeToDataPackSnbt(value: T) = encodeToDataPackSnbt(serializer<T>(), value)
fun <T> encodeToDataPackSnbt(serializer: KSerializer<T>, value: T) = encodeToDataPackNbtTag(serializer, value).toSnbt()

/** Encodes a data pack resource to an NBT tag like its data pack file, see [encodeToDataPackSnbt]. */
inline fun <reified T> encodeToDataPackNbtTag(value: T) = encodeToDataPackNbtTag(serializer<T>(), value)
fun <T> encodeToDataPackNbtTag(serializer: KSerializer<T>, value: T) = encodeToNbtTag(DataPackShapedSerializer(serializer), value)

private fun StringBuilder.appendSnbt(tag: NbtTag): StringBuilder = when (tag) {
	is NbtCompound -> {
		append('{')
		tag.entries.forEachIndexed { index, (key, value) ->
			if (index > 0) append(',')
			appendSnbtString(key, isKey = true).append(':').appendSnbt(value)
		}
		append('}')
	}

	is NbtList<*> -> {
		append('[')
		tag.forEachIndexed { index, value ->
			if (index > 0) append(',')
			appendSnbt(value)
		}
		append(']')
	}

	is NbtString -> appendSnbtString(tag.value, isKey = false)
	else -> append(tag.toString())
}

private fun Char.isSnbtBare() = this == '-' || this == '_' || this in 'a'..'z' || this in 'A'..'Z' || this in '0'..'9'

/** A bare token starting with a digit, `+`, `-` or `.` reads as a number, and `true`/`false` (any case) as a byte. */
private fun String.isBareSnbtValue() =
	isNotEmpty() && (this[0] == '_' || this[0] in 'a'..'z' || this[0] in 'A'..'Z') && all(Char::isSnbtBare) &&
		!equals("true", ignoreCase = true) && !equals("false", ignoreCase = true)

/**
 * Writes a key bare when it only holds `[A-Za-z0-9_-]` and a value bare when [isBareSnbtValue], else quoted with full
 * escaping (single quotes around a key holding only `"`).
 */
private fun StringBuilder.appendSnbtString(value: String, isKey: Boolean): StringBuilder {
	val isBare = if (isKey) value.isNotEmpty() && value.all(Char::isSnbtBare) else value.isBareSnbtValue()
	if (isBare) return append(value)

	val quote = if (isKey && '"' in value && '\'' !in value) '\'' else '"'
	append(quote)
	value.forEach {
		when (it) {
			'\\', quote -> append('\\').append(it)
			'\n' -> append("\\n")
			'\r' -> append("\\r")
			else -> append(it)
		}
	}
	return append(quote)
}

/**
 * Parses an SNBT compound, the textual NBT form Minecraft accepts in commands: `"{Count:3b,tag:{x:1}}".toNbt()`.
 *
 * Use it to reuse a snippet copied from a command or the wiki where the DSL expects an [NbtCompound], instead of
 * transcribing it into an [nbt] builder.
 *
 * @throws kotlinx.serialization.SerializationException when the string is not a valid SNBT compound.
 */
fun String.toNbt() = snbtSerializer.decodeFromString<NbtCompound>(this)

/** Parses any SNBT value, including lists, arrays, and bare primitives. */
fun String.toNbtTag() = snbtSerializer.decodeFromString<NbtTag>(this)

// endregion
