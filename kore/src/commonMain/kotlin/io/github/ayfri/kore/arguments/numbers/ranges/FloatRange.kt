package io.github.ayfri.kore.arguments.numbers.ranges

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.numbers.toStringTruncatedIfRound
import kotlinx.serialization.Serializable

interface FloatingRange : Range

@Serializable(with = Argument.ArgumentSerializer::class)
data class FloatRange(val start: Double?, val end: Double?) : FloatingRange {
	override fun asString() = when {
		start == null && end == null -> ""
		start == null -> "..${end?.toStringTruncatedIfRound()}"
		end == null -> "${start.toStringTruncatedIfRound()}.."
		else -> "${start.toStringTruncatedIfRound()}..${end.toStringTruncatedIfRound()}"
	}

	override fun toString() = asString()

	fun asRangeOrDouble() = FloatRangeOrFloat(this)
}

@Serializable(with = Argument.ArgumentSerializer::class)
data class FloatRangeOrFloat(val range: FloatRange? = null, val double: Double? = null) : FloatingRange {
	override fun asString() = range?.toString() ?: double.toString()

	override fun toString() = asString()

	fun asRange() = range ?: FloatRange(double, double)
}

fun range(start: Double, end: Double) = FloatRange(start, end)
fun rangeStart(start: Double) = FloatRange(start, null)
fun rangeEnd(end: Double) = FloatRange(null, end)
fun range(range: ClosedFloatingPointRange<Double>) = FloatRange(range.start, range.endInclusive)
fun ClosedFloatingPointRange<Double>.asRange() = FloatRange(start, endInclusive)

fun rangeOrDouble(start: Double, end: Double) = FloatRangeOrFloat(FloatRange(start, end))
fun rangeOrDouble(range: FloatRange) = FloatRangeOrFloat(range)
fun rangeOrDouble(double: Double) = FloatRangeOrFloat(double = double)
fun rangeOrDoubleStart(double: Double) = FloatRangeOrFloat(FloatRange(double, null))
fun rangeOrDoubleEnd(double: Double) = FloatRangeOrFloat(FloatRange(null, double))
fun Double.asRangeOrDouble() = FloatRangeOrFloat(double = this)
fun ClosedFloatingPointRange<Double>.asRangeOrDouble() = FloatRangeOrFloat(FloatRange(start, endInclusive))
