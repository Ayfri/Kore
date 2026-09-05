package io.github.ayfri.kore.strings

import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.commands.data
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

/**
 * One operand of a concatenation: either a compile-time [Literal] or the runtime content of a
 * [Ref]erenced [DynamicString]. Replaces the untyped `Any` operands the module used to accept.
 */
sealed interface StringPart {
	data class Literal(val value: String) : StringPart
	data class Ref(val string: DynamicString) : StringPart
}

/** Wraps this literal as a concatenation operand. */
val String.asStringPart: StringPart get() = StringPart.Literal(this)

/** Wraps this dynamic string as a concatenation operand. */
val DynamicString.asStringPart: StringPart get() = StringPart.Ref(this)

internal fun DynamicStringRuntime.concatHelper(): FunctionWithMacros<ConcatMacros> =
	ensure(OopConstants.stringConcatMacroName, ::ConcatMacros) {
		data(libStorageArg) {
			modify(heapPath(macros.dst), "${macros.a}${macros.b}")
		}
	}

private fun invokeConcat(fn: Function, a: StringPart, b: StringPart, target: DynamicString) {
	val rt = target.runtime
	rt.concatHelper()
	val args = rt.argsPath(OopConstants.stringConcatMacroName)
	fn.data(rt.libStorageArg) {
		when (a) {
			is StringPart.Literal -> modify("$args.a", a.value)
			is StringPart.Ref -> modify("$args.a") { set(a.string.storage, a.string.nbtPath) }
		}
		when (b) {
			is StringPart.Literal -> modify("$args.b", b.value)
			is StringPart.Ref -> modify("$args.b") { set(b.string.storage, b.string.nbtPath) }
		}
		modify("$args.dst", target.name)
	}
	fn.callMacro(OopConstants.stringConcatMacroName, rt.libStorageArg, args)
}

/** Appends [value] at the end of this dynamic string (in place). */
context(fn: Function)
fun DynamicString.append(value: String) = invokeConcat(fn, asStringPart, value.asStringPart, this)

/** Appends the content of [other] at the end of this dynamic string (in place). */
context(fn: Function)
fun DynamicString.append(other: DynamicString) = appendFrom(other)

/** Writes `a + b` (both literal) into [target]. */
context(fn: Function)
fun concat(target: DynamicString, a: String, b: String) = invokeConcat(fn, a.asStringPart, b.asStringPart, target)

/** Writes `a + b` into [target], with [a] as a literal and [b] as a [DynamicString]. */
context(fn: Function)
fun concat(target: DynamicString, a: String, b: DynamicString) =
	invokeConcat(fn, a.asStringPart, b.asStringPart, target)

/** Writes `a + b` into [target], with [a] as a [DynamicString] and [b] as a literal. */
context(fn: Function)
fun concat(target: DynamicString, a: DynamicString, b: String) =
	invokeConcat(fn, a.asStringPart, b.asStringPart, target)

/** Writes `a + b` into [target], reading [a] and [b] as [DynamicString]s. */
context(fn: Function)
fun concat(target: DynamicString, a: DynamicString, b: DynamicString) =
	invokeConcat(fn, a.asStringPart, b.asStringPart, target)

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
		is StringPart.Ref -> target.setFrom(first.string)
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
operator fun DynamicString.plusAssign(value: String) = append(value)

/** Prepends the content of [other] at the beginning of this dynamic string (in place). */
context(fn: Function)
fun DynamicString.prepend(other: DynamicString) = prependFrom(other)

/** Prepends [value] at the beginning of this dynamic string (in place). */
context(fn: Function)
fun DynamicString.prepend(value: String) = invokeConcat(fn, value.asStringPart, asStringPart, this)
