package io.github.ayfri.kore.arguments.numbers

import kotlin.math.truncate

val Double.truncated get() = truncate(this)

fun Double.toStringTruncatedIfRound() = when {
	this % 1 == 0.0 -> toLong().toString()
	else -> toString()
}

fun Double.toStringTruncated() = toLong().toString()

internal val Double.truncateIfRoundEmptyIfZero
	get() = when {
		this == 0.0 -> ""
		else -> toLong().toString()
	}
