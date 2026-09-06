package io.github.ayfri.kore.strings

import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.scoreboard.ScoreboardEntity

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

/**
 * Builder handed to [DynamicString.build], where every `+` appends one operand to the target.
 *
 * ```
 * summary.build {
 * 	+"Player "
 * 	+playerName
 * 	+": "
 * 	+kills
 * }
 * ```
 */
class DynamicStringBuilder internal constructor(private val target: DynamicString, private val fn: Function) {
	private val pendingLiteral = StringBuilder()
	private var started = false

	/** Appends the literal value of this string. */
	operator fun String.unaryPlus() {
		pendingLiteral.append(this)
	}

	/** Appends this single character. */
	operator fun Char.unaryPlus() {
		pendingLiteral.append(this)
	}

	/** Appends the runtime content of this dynamic string. */
	operator fun DynamicString.unaryPlus() = context(fn) {
		flush()
		if (started) target.append(this@unaryPlus) else if (target != this@unaryPlus) target.setFrom(this@unaryPlus)
		started = true
	}

	/** Appends the decimal rendering of this score. */
	operator fun ScoreboardEntity.unaryPlus() = context(fn) {
		flush()
		if (started) target.appendFrom(this@unaryPlus) else target.setFrom(this@unaryPlus)
		started = true
	}

	/** Consecutive literals are folded into a single operand, and the first one seeds the target with a plain `set value`. */
	private fun flush() = context(fn) {
		if (pendingLiteral.isEmpty()) return@context
		val value = pendingLiteral.toString()
		pendingLiteral.clear()
		if (started) target.append(value) else target.set(value)
		started = true
	}

	internal fun seal() = context(fn) {
		flush()
		if (!started) target.set("")
	}
}

/**
 * Rebuilds this string from the operands pushed inside [block].
 *
 * Replaces chains of `set` / `append` calls with a single readable block mixing literals, other
 * dynamic strings and scores.
 *
 * ```
 * // playerName holds "Ayfri" and kills holds 3 at runtime
 * summary.build { +"Player "; +playerName; +": "; +kills }  // "Player Ayfri: 3"
 * ```
 */
context(fn: Function)
fun DynamicString.build(block: DynamicStringBuilder.() -> Unit): DynamicString {
	DynamicStringBuilder(this, fn).apply(block).seal()
	return this
}

/**
 * Concatenation as an expression: writes `this + other` into a fresh anonymous slot and returns it,
 * leaving both operands untouched.
 *
 * ```
 * val full = first + " " + last
 * ```
 */
context(fn: Function)
operator fun DynamicString.plus(other: String): DynamicString = copy().also { it.append(other) }

/** Expression concatenation with the runtime content of [other]. See [plus]. */
context(fn: Function)
operator fun DynamicString.plus(other: DynamicString): DynamicString = copy().also { it.append(other) }

/** Expression concatenation with the decimal rendering of [score]. See [plus]. */
context(fn: Function)
operator fun DynamicString.plus(score: ScoreboardEntity): DynamicString = copy().also { it.appendFrom(score) }

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

/** Operator alias for [append] when the right operand is a single character. */
context(fn: Function)
operator fun DynamicString.plusAssign(char: Char) {
	append(char.toString())
}

/** Operator alias for [appendFrom], rendering [score] as its decimal value. */
context(fn: Function)
operator fun DynamicString.plusAssign(score: ScoreboardEntity) {
	appendFrom(score)
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
