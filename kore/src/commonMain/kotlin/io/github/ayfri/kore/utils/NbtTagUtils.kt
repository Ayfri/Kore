package io.github.ayfri.kore.utils

import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.decodeFromString
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
 */
fun NbtTag.toSnbt(): String = buildString { appendSnbt(this@toSnbt) }

/** Encodes [value] to SNBT with [snbtSerializer], see [toSnbt]. */
inline fun <reified T> encodeToSnbt(value: T) = snbtSerializer.encodeToNbtTag(value).toSnbt()
fun <T> encodeToSnbt(serializer: SerializationStrategy<T>, value: T) = snbtSerializer.encodeToNbtTag(serializer, value).toSnbt()

private fun StringBuilder.appendSnbt(tag: NbtTag): StringBuilder = when (tag) {
	is NbtCompound -> {
		append('{')
		tag.entries.forEachIndexed { index, (key, value) ->
			if (index > 0) append(',')
			appendSnbtString(key, forceQuote = false).append(':').appendSnbt(value)
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

	is NbtString -> appendSnbtString(tag.value, forceQuote = true)
	else -> append(tag.toString())
}

/** Same quoting choices as knbt (bare safe keys, single quotes around a key holding only `"`), with full escaping. */
private fun StringBuilder.appendSnbtString(value: String, forceQuote: Boolean): StringBuilder {
	val isSafe = value.isNotEmpty() && value.all { it == '-' || it == '_' || it in 'a'..'z' || it in 'A'..'Z' || it in '0'..'9' }
	if (!forceQuote && isSafe) return append(value)

	val quote = if (!forceQuote && '"' in value && '\'' !in value) '\'' else '"'
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
