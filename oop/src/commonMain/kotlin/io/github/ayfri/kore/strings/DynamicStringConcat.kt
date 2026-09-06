package io.github.ayfri.kore.strings

import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.functions.Function

/** Scratch slot staging an operand that vanilla cannot read directly (a literal, or an aliased source). */
internal const val CONCAT_TMP_KEY = "${INTERNAL_NAME_PREFIX}concat_tmp"

/**
 * One operand of a concatenation: either a compile-time [Literal] or the runtime content of a
 * [Ref]erenced [DynamicString].
 */
sealed interface StringPart {
	data class Literal(val value: String) : StringPart
	data class Ref(val string: DynamicString) : StringPart
}

/** Wraps this literal as a concatenation operand. */
val String.asStringPart: StringPart get() = StringPart.Literal(this)

/** Wraps this dynamic string as a concatenation operand. */
val DynamicString.asStringPart: StringPart get() = StringPart.Ref(this)

/**
 * Copies [value] into the shared scratch slot, the only way to feed a literal to vanilla's
 * `append string` / `prepend string`, which both require an NBT source path.
 */
private fun DynamicString.stageLiteral(fn: Function, value: String): String {
	val tmp = runtime.tmpPath(CONCAT_TMP_KEY)
	fn.setNbtString(storage, tmp, value)
	return tmp
}

/** Same staging for a [DynamicString] operand, used when source and destination are the same slot. */
private fun DynamicString.stageSelf(fn: Function): String {
	val tmp = runtime.tmpPath(CONCAT_TMP_KEY)
	fn.copyNbt(storage, tmp, storage, nbtPath)
	return tmp
}

/**
 * Appends [value] at the end of this dynamic string (in place).
 *
 * ```
 * "kore".append(" lib")  // "kore lib"
 * ```
 */
context(fn: Function)
fun DynamicString.append(value: String): Command {
	val tmp = stageLiteral(fn, value)
	return fn.data(storage) { modify(nbtPath) { append(storage, tmp, null, null) } }
}

/**
 * Appends the content of [other] at the end of this dynamic string (in place).
 *
 * ```
 * // other holds " lib" at runtime
 * "kore".append(other)  // "kore lib"
 * ```
 */
context(fn: Function)
fun DynamicString.append(other: DynamicString): Command {
	if (other != this) return appendFrom(other)
	val tmp = stageSelf(fn)
	return fn.data(storage) { modify(nbtPath) { append(storage, tmp, null, null) } }
}

/** Writes `a + b` (both literal) into [target]. Folded at generation time into a single `set value`. */
context(fn: Function)
fun concat(target: DynamicString, a: String, b: String) = target.set(a + b)

/** Writes `a + b` into [target], with [a] as a literal and [b] as a [DynamicString]. */
context(fn: Function)
fun concat(target: DynamicString, a: String, b: DynamicString) = when (target) {
	b -> target.prepend(a)
	else -> {
		target.set(a)
		target.appendFrom(b)
	}
}

/** Writes `a + b` into [target], with [a] as a [DynamicString] and [b] as a literal. */
context(fn: Function)
fun concat(target: DynamicString, a: DynamicString, b: String) {
	if (target != a) target.setFrom(a)
	target.append(b)
}

/** Writes `a + b` into [target], reading [a] and [b] as [DynamicString]s. */
context(fn: Function)
fun concat(target: DynamicString, a: DynamicString, b: DynamicString) = when (target) {
	a -> target.append(b)
	b -> target.prepend(a)
	else -> {
		target.setFrom(a)
		target.appendFrom(b)
	}
}

/**
 * N-ary concatenation: writes the concatenation of [parts] into [target] by seeding it with the
 * first part then appending the remaining ones. Passing no part clears [target].
 */
context(fn: Function)
fun concatAll(target: DynamicString, vararg parts: StringPart) {
	val first = parts.firstOrNull()
	if (first == null) {
		target.set("")
		return
	}
	when (first) {
		is StringPart.Literal -> target.set(first.value)
		is StringPart.Ref -> if (target != first.string) target.setFrom(first.string)
	}
	parts.drop(1).forEach { part ->
		when (part) {
			is StringPart.Literal -> target.append(part.value)
			is StringPart.Ref -> target.append(part.string)
		}
	}
}

/** Operator alias for [append] when the right operand is another [DynamicString]. */
context(fn: Function)
operator fun DynamicString.plusAssign(other: DynamicString) {
	append(other)
}

/** Operator alias for [append] when the right operand is a literal. */
context(fn: Function)
operator fun DynamicString.plusAssign(value: String) {
	append(value)
}

/**
 * Prepends the content of [other] at the beginning of this dynamic string (in place).
 *
 * ```
 * // other holds "kore " at runtime
 * "lib".prepend(other)  // "kore lib"
 * ```
 */
context(fn: Function)
fun DynamicString.prepend(other: DynamicString): Command {
	if (other != this) return prependFrom(other)
	val tmp = stageSelf(fn)
	return fn.data(storage) { modify(nbtPath) { prepend(storage, tmp, null, null) } }
}

/**
 * Prepends [value] at the beginning of this dynamic string (in place).
 *
 * ```
 * "lib".prepend("kore ")  // "kore lib"
 * ```
 */
context(fn: Function)
fun DynamicString.prepend(value: String): Command {
	val tmp = stageLiteral(fn, value)
	return fn.data(storage) { modify(nbtPath) { prepend(storage, tmp, null, null) } }
}
