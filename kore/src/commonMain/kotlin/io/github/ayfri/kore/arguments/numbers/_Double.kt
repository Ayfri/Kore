package io.github.ayfri.kore.arguments.numbers

import kotlin.math.truncate

val Double.truncated get() = truncate(this)

/** Like `toString()`, but always keeps a decimal point (JVM does this by default, JS doesn't). */
val Double.toStringWithDecimal
	get() = toString().let { if ('.' !in it && 'E' !in it && 'e' !in it) "$it.0" else it }

fun Double.toStringTruncatedIfRound() = when {
	this % 1 == 0.0 -> toLong().toString()
	else -> toString()
}

fun Double.toStringTruncated() = toLong().toString()

internal val Double.truncateIfRoundEmptyIfZero
	get() = when {
		this == 0.0 -> ""
		this % 1 == 0.0 -> toLong().toString()
		else -> toString()
	}
