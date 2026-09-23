package io.github.ayfri.kore.arguments.numbers

import kotlin.math.truncate

val Double.truncated get() = truncate(this)

/**
 * Like `toString()` but never in scientific notation, which Brigadier's number reader rejects.
 *
 * ```kotlin
 * (-3.0E7).toPlainString() // "-30000000.0"
 * 1.0E-4.toPlainString() // "0.0001"
 * ```
 */
fun Double.toPlainString(): String {
	val raw = toString()
	val exponentIndex = raw.indexOfFirst { it == 'E' || it == 'e' }
	if (exponentIndex == -1) return raw

	val negative = raw.startsWith('-')
	val mantissa = raw.substring(if (negative) 1 else 0, exponentIndex)
	val digits = mantissa.replace(".", "")
	val pointIndex = (mantissa.indexOf('.').takeIf { it >= 0 } ?: mantissa.length) + raw.substring(exponentIndex + 1).toInt()
	val plain = when {
		pointIndex <= 0 -> "0.${"0".repeat(-pointIndex)}$digits".trimEnd('0')
		pointIndex >= digits.length -> "$digits${"0".repeat(pointIndex - digits.length)}.0"
		else -> "${digits.take(pointIndex)}.${digits.drop(pointIndex)}".trimEnd('0')
	}
	return "${if (negative) "-" else ""}${plain.removeSuffix(".")}${if (plain.endsWith('.')) ".0" else ""}"
}

/** Like [toPlainString], but always keeps a decimal point (JVM does this by default, JS doesn't). */
val Double.toStringWithDecimal
	get() = toPlainString().let { if ('.' !in it) "$it.0" else it }

fun Double.toStringTruncatedIfRound() = when {
	this % 1 == 0.0 -> toLong().toString()
	else -> toPlainString()
}

fun Double.toStringTruncated() = toLong().toString()

internal val Double.truncateIfRoundEmptyIfZero
	get() = when {
		this == 0.0 -> ""
		this % 1 == 0.0 -> toLong().toString()
		else -> toPlainString()
	}
