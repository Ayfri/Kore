package io.github.ayfri.kore.strings

import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.FunctionWithMacros
import io.github.ayfri.kore.functions.Macros
import io.github.ayfri.kore.functions.getValue

/** Macro identifiers for the shared `kore_string_concat` helper. */
class ConcatMacros internal constructor() : Macros() {
	val a by "a"
	val b by "b"
	val dst by "dst"
}

internal fun DynamicStringRuntime.concatHelper(): FunctionWithMacros<ConcatMacros> =
	ensure(OopConstants.stringConcatMacroName, ::ConcatMacros) {
		data(libStorageArg) {
			modify(heapPath(macros.dst), "${macros.a}${macros.b}")
		}
	}

private const val ARGS_A = "a"
private const val ARGS_B = "b"
private const val ARGS_DST = "dst"

private fun invokeConcat(
	fn: Function,
	aValue: Any,
	bValue: Any,
	aIsRef: Boolean,
	bIsRef: Boolean,
	target: DynamicString,
) {
	fn.datapack.requireDynamicStringRuntime().concatHelper()
	val args = argsPath(OopConstants.stringConcatMacroName)
	fn.data(target.storage) {
		if (aIsRef) modify("$args.$ARGS_A") { set(target.storage, (aValue as DynamicString).nbtPath) }
		else modify("$args.$ARGS_A", aValue as String)
		if (bIsRef) modify("$args.$ARGS_B") { set(target.storage, (bValue as DynamicString).nbtPath) }
		else modify("$args.$ARGS_B", bValue as String)
		modify("$args.$ARGS_DST", target.name)
	}
	fn.function(
		namespace = fn.datapack.name,
		name = OopConstants.stringConcatMacroName,
		arguments = target.storage,
		path = args
	)
}

/** Writes `a + b` (both literal) into [target]. */
context(fn: Function)
fun concat(target: DynamicString, a: String, b: String) = invokeConcat(fn, a, b, false, false, target)

/** Writes `a + b` into [target], reading [a] and [b] as [DynamicString]s. */
context(fn: Function)
fun concat(target: DynamicString, a: DynamicString, b: DynamicString) = invokeConcat(fn, a, b, true, true, target)

/** Writes `a + b` into [target], with [a] as a [DynamicString] and [b] as a literal. */
context(fn: Function)
fun concat(target: DynamicString, a: DynamicString, b: String) = invokeConcat(fn, a, b, true, false, target)

/** Writes `a + b` into [target], with [a] as a literal and [b] as a [DynamicString]. */
context(fn: Function)
fun concat(target: DynamicString, a: String, b: DynamicString) = invokeConcat(fn, a, b, false, true, target)

/**
 * N-ary concatenation: concatenates the provided [parts] (either [String] literals or [DynamicString])
 * into [target]. Works by folding with `concat(target, target, part)`.
 */
context(fn: Function)
fun concatAll(target: DynamicString, vararg parts: Any) {
	require(parts.all { it is String || it is DynamicString }) {
		"concatAll only accepts String or DynamicString parts, got ${parts.filterNot { it is String || it is DynamicString }}"
	}
	if (parts.isEmpty()) {
		target.set("")
		return
	}
	when (val first = parts[0]) {
		is String -> target.set(first)
		is DynamicString -> target.setFrom(first)
	}
	parts.drop(1).forEach { part ->
		when (part) {
			is String -> target.append(part)
			is DynamicString -> target.append(part)
		}
	}
}

/** Appends [value] at the end of this dynamic string (in place). */
context(fn: Function)
fun DynamicString.append(value: String) = invokeConcat(fn, this, value, aIsRef = true, bIsRef = false, target = this)

/** Appends the content of [other] at the end of this dynamic string (in place). */
context(fn: Function)
fun DynamicString.append(other: DynamicString) = appendFrom(other)

/** Prepends [value] at the beginning of this dynamic string (in place). */
context(fn: Function)
fun DynamicString.prepend(value: String) = invokeConcat(fn, value, this, aIsRef = false, bIsRef = true, target = this)

/** Prepends the content of [other] at the beginning of this dynamic string (in place). */
context(fn: Function)
fun DynamicString.prepend(other: DynamicString) = prependFrom(other)

/** Operator alias for [append] when the right operand is a literal. */
context(fn: Function)
operator fun DynamicString.plusAssign(value: String) = append(value)

/** Operator alias for [append] when the right operand is another [DynamicString]. */
context(fn: Function)
operator fun DynamicString.plusAssign(other: DynamicString) {
	append(other)
}
