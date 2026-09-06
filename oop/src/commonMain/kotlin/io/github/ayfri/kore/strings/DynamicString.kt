package io.github.ayfri.kore.strings

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.nbtComponent
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.StorageArgument
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.functions.Function

/**
 * High level wrapper around a persistent NBT string stored inside the Kore string library storage.
 *
 * A [DynamicString] represents a named slot located at `<storage> <heapRoot>.<name>` (by default
 * `kore_string_lib:memory heap.<name>`) and exposes idiomatic Kotlin methods that translate to the
 * underlying `data` / `execute` / macro commands. Every path is resolved through [runtime], so a
 * custom [DynamicStringConfig] moves the whole module at once.
 */
class DynamicString internal constructor(val name: String, val runtime: DynamicStringRuntime) {
	/** Full NBT path of the backing string, for example `heap.myvar`. */
	val nbtPath: String get() = runtime.heapPath(name)
	val storage: StorageArgument get() = runtime.libStorageArg

	override fun equals(other: Any?) = other is DynamicString && other.name == name && other.runtime === runtime
	override fun hashCode() = name.hashCode()
	override fun toString() = "DynamicString($name)"
}

/**
 * Creates a new [DynamicString] pointing at `<storage> <heapRoot>.<name>`, allocating the name
 * against the runtime so collisions with a previously declared string throw a descriptive error.
 */
fun DynamicStringRuntime.dynamicString(name: String): DynamicString {
	allocateString(name)
	return DynamicString(name, this)
}

/** Creates a new [DynamicString] in the context of the given [DataPack], allocating its name. */
fun DataPack.dynamicString(name: String): DynamicString = requireDynamicStringRuntime().dynamicString(name)

/**
 * Allocates a fresh anonymous [DynamicString] scratch slot, the backing store of every expression
 * style helper ([DynamicString.plus], [DynamicString.get], [DynamicString.uppercased], ...).
 *
 * Each call returns a distinct slot, so intermediate results never clobber each other.
 */
fun DynamicStringRuntime.tempString(): DynamicString = scratchString("${INTERNAL_NAME_PREFIX}temp_${nextTempId()}")

/** Allocates a fresh anonymous [DynamicString] scratch slot in the context of the given [DataPack]. */
fun DataPack.tempDynamicString(): DynamicString = requireDynamicStringRuntime().tempString()

/**
 * Copies this string into a fresh anonymous slot, the entry point for chaining transformations
 * without touching the original.
 *
 * ```
 * val shouted = name.copy().also { it.uppercase() }
 * ```
 */
context(fn: Function)
fun DynamicString.copy(): DynamicString = runtime.tempString().also { it.setFrom(this) }

/** Exposes this storage-backed string as an NBT chat component consumable by chat-like arguments. */
fun DynamicString.asChatComponents(interpret: Boolean = true): ChatComponents = nbtComponent(nbtPath, storage) {
	this.interpret = interpret
}

/**
 * Appends [other] at the end of this string using vanilla `append string`.
 *
 * If [start]/[end] are provided, only the requested substring of [other] is appended.
 */
context(fn: Function)
fun DynamicString.appendFrom(other: DynamicString, start: Int? = null, end: Int? = null) = fn.data(storage) {
	modify(nbtPath) { append(other.storage, other.nbtPath, start, end) }
}

/** Removes this string from the backing storage. */
context(fn: Function)
fun DynamicString.clear() = fn.data(storage) {
	remove(nbtPath)
}

/** Copies this string into [target]. */
context(fn: Function)
fun DynamicString.copyTo(target: DynamicString) = target.setFrom(this)

/**
 * Measures the length of this string into a score and returns it.
 *
 * ```
 * "kore".length()  // 4
 * ```
 */
context(fn: Function)
fun DynamicString.length(holder: String = runtime.config.lengthHolder): DynamicStringResult {
	fn.execute {
		storeResult { score(literal(holder), runtime.config.lengthObjective) }
		run {
			data(storage) {
				get(nbtPath)
			}
		}
	}
	return DynamicStringResult(holder, runtime.config.lengthObjective)
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

/** Sets this string to the given literal [value]. */
context(fn: Function)
fun DynamicString.set(value: String) = fn.data(storage) {
	modify(nbtPath, value)
}

/** Copies the content of [other] into this string (alias of [setFrom]). */
context(fn: Function)
fun DynamicString.set(other: DynamicString) = setFrom(other)

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
