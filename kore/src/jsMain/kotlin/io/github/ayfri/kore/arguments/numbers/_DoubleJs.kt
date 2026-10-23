package io.github.ayfri.kore.arguments.numbers

private fun fround(value: Double): Double = js("Math.fround(value)").unsafeCast<Double>()

private fun Double.toPrecision(digits: Int): String = asDynamic().toPrecision(digits).unsafeCast<String>()

private fun Double.roundTripsAsFloat(digits: Int) = fround(toPrecision(digits).toDouble()) == this

/**
 * Kotlin/JS keeps a float as a double without rounding it to float precision, so the digits of the double would show.
 * Like Java, the shortest digits that read back as this float, the even last digit when two are equally close, and the
 * closest of the two-digit forms when one digit is enough (`Float.MIN_VALUE` is `1.4E-45`, not `1.0E-45`).
 */
internal actual fun Float.toCanonicalString(): String {
	val value = fround(toDouble())
	if (!value.isFinite()) return value.toString()
	if (value == 0.0) return if (1 / value < 0) "-0.0" else "0.0"

	val shortest = (1..9).first { value.roundTripsAsFloat(it) }
	val length = if (shortest == 1 && value.roundTripsAsFloat(2)) 2 else shortest
	val rounded = value.toPrecision(length)
	val next = value.toPrecision(length + 1)
	val isTie = next.significantDigits().endsWith('5') && value.toPrecision(length + 30).significantDigits().trimEnd('0') == next.significantDigits()
	if (!isTie || rounded.significantDigits().last().digitToInt() % 2 == 0) return rounded.inJavaLayout()

	val mantissa = next.substringBefore('e')
	return (mantissa.dropLast(1) + next.substring(mantissa.length)).inJavaLayout()
}

private fun String.significantDigits() = substringBefore('e').replace(".", "").removePrefix("-").trimStart('0')

/** Like Java, the shortest digits, or the closest of the two-digit forms when one digit is enough (`Double.MIN_VALUE` is `4.9E-324`). */
internal actual fun Double.toCanonicalString(): String = when {
	!isFinite() -> toString()
	this == 0.0 -> if (1 / this < 0) "-0.0" else "0.0"
	else -> toString().let { shortest ->
		val twoDigits = toPrecision(2)
		if (shortest.significantDigits().trimEnd('0').length == 1 && twoDigits.toDouble() == this) twoDigits else shortest
	}.inJavaLayout()
}

/** Rewrites the shortest digits of a number (`1`, `0.00001`, `1e+21`) as Java prints them (`1.0`, `1.0E-5`, `1.0E21`). */
private fun String.inJavaLayout(): String {
	val negative = startsWith('-')
	val unsigned = removePrefix("-")
	val exponentIndex = unsigned.indexOf('e')
	val mantissa = if (exponentIndex == -1) unsigned else unsigned.substring(0, exponentIndex)
	val exponent = if (exponentIndex == -1) 0 else unsigned.substring(exponentIndex + 1).removePrefix("+").toInt()

	val integerLength = mantissa.indexOf('.').let { if (it == -1) mantissa.length else it }
	val allDigits = mantissa.replace(".", "")
	val digits = allDigits.trimStart('0').trimEnd('0')

	val pointIndex = integerLength + exponent - (allDigits.length - allDigits.trimStart('0').length)
	val sign = if (negative) "-" else ""
	return sign + when {
		pointIndex <= -3 || pointIndex > 7 -> "${digits[0]}.${digits.drop(1).ifEmpty { "0" }}E${pointIndex - 1}"
		pointIndex <= 0 -> "0.${"0".repeat(-pointIndex)}$digits"
		pointIndex >= digits.length -> "$digits${"0".repeat(pointIndex - digits.length)}.0"
		else -> "${digits.take(pointIndex)}.${digits.drop(pointIndex)}"
	}
}
