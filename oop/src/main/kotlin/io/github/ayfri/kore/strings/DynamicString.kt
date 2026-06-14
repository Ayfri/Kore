package io.github.ayfri.kore.strings

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.nbtComponent
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.storage
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.functions.Function

/**
 * High level wrapper around a persistent NBT string stored inside the Kore string library storage.
 *
 * A [DynamicString] represents a named slot located at `kore_string_lib:memory heap.<name>` and
 * exposes idiomatic Kotlin methods that translate to the underlying `data` / `execute` / macro
 * commands required by Minecraft 1.21.11 to perform string manipulation inside a data pack.
 */
open class DynamicString(val name: String) {
	val storage = storage(OopConstants.stringStorageName, OopConstants.stringStorageNamespace)

	/** Full NBT path of the backing string, for example `heap.myvar`. */
	val nbtPath: String = "heap.$name"
}

/**
 * Creates a new [DynamicString] pointing at `kore_string_lib:memory heap.<name>`.
 *
 * When [rt] is provided the name is allocated against the runtime, throwing a descriptive error on
 * collisions with a previously declared dynamic string.
 */
fun dynamicString(name: String, rt: DynamicStringRuntime? = null): DynamicString {
	rt?.allocateString(name)
	return DynamicString(name)
}

/** Creates a new [DynamicString] in the context of the given [DataPack], allocating its name. */
fun DataPack.dynamicString(name: String): DynamicString = dynamicString(name, requireDynamicStringRuntime())

/** Exposes this storage-backed string as an NBT chat component consumable by chat-like arguments. */
fun DynamicString.asChatComponents(interpret: Boolean = true): ChatComponents = nbtComponent(nbtPath, storage) {
	this.interpret = interpret
}

/** Sets this string to the given literal [value]. */
context(fn: Function)
fun DynamicString.set(value: String) = fn.data(storage) {
	modify(nbtPath, value)
}

/** Copies the content of [other] into this string. */
context(fn: Function)
fun DynamicString.setFrom(other: DynamicString) = fn.data(storage) {
	modify(nbtPath) { set(other.storage, other.nbtPath) }
}

/**
 * Replaces this string with a substring of [other], using static bounds.
 *
 * Equivalent to `set string ... [start] [end]` in vanilla commands.
 */
context(fn: Function)
fun DynamicString.setFrom(other: DynamicString, start: Int, end: Int? = null) = fn.data(storage) {
	modify(nbtPath) { set(other.storage, other.nbtPath, start, end) }
}

/** Copies the content of [other] into this string (alias of [setFrom]). */
context(fn: Function)
fun DynamicString.set(other: DynamicString) = setFrom(other)

/** Removes this string from the backing storage. */
context(fn: Function)
fun DynamicString.clear() = fn.data(storage) {
	remove(nbtPath)
}

/** Copies this string into [target]. */
context(fn: Function)
fun DynamicString.copyTo(target: DynamicString) = target.setFrom(this)

/**
 * Appends [other] at the end of this string using vanilla `append string`.
 *
 * If [start]/[end] are provided, only the requested substring of [other] is appended.
 */
context(fn: Function)
fun DynamicString.appendFrom(other: DynamicString, start: Int? = null, end: Int? = null) = fn.data(storage) {
	modify(nbtPath) { append(other.storage, other.nbtPath, start, end) }
}

/**
 * Prepends [other] at the beginning of this string using vanilla `prepend string`.
 *
 * If [start]/[end] are provided, only the requested substring of [other] is prepended.
 */
context(fn: Function)
fun DynamicString.prependFrom(other: DynamicString, start: Int? = null, end: Int? = null) = fn.data(storage) {
	modify(nbtPath) { prepend(other.storage, other.nbtPath, start, end) }
}

/**
 * Stores the length of this string into [holder] on the `kore_string_len` objective using
 * `execute store result`. Returns [holder] so callers can chain the resulting score.
 */
context(fn: Function)
fun DynamicString.length(holder: String = OopConstants.stringLengthHolder): String {
	fn.execute {
		storeResult { score(literal(holder), OopConstants.stringLengthObjective) }
		run {
			data(storage) {
				get(nbtPath)
			}
		}
	}
	return holder
}

/** Full typed handle to the score produced by [length]. */
data class DynamicStringLength(val holder: String, val objective: String = OopConstants.stringLengthObjective)

/**
 * Typed variant of [length] that returns both holder and objective, handy when multiple length
 * scores are kept alive simultaneously (use [holder] to avoid clobbering the default one).
 */
context(fn: Function)
fun DynamicString.lengthScore(holder: String = OopConstants.stringLengthHolder): DynamicStringLength {
	length(holder)
	return DynamicStringLength(holder)
}
