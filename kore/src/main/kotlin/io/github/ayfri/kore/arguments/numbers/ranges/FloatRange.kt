package io.github.ayfri.kore.arguments.numbers.ranges

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.numbers.str
import kotlinx.serialization.Serializable

interface FloatingRange : Range

@Serializable(with = Argument.ArgumentSerializer::class)
data class FloatRange(val start: Double?, val end: Double?) : FloatingRange {
	override fun asString() = when {
		start == null && end == null -> ""
		start == null -> "..${end?.str}"
		end == null -> "${start.str}.."
		else -> "${start.str}..${end.str}"
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

inline fun range(start: Double, end: Double) = FloatRange(start, end)
inline fun rangeStart(start: Double) = FloatRange(start, null)
inline fun rangeEnd(end: Double) = FloatRange(null, end)
inline fun range(range: ClosedFloatingPointRange<Double>) = FloatRange(range.start, range.endInclusive)
inline fun ClosedFloatingPointRange<Double>.asRange() = FloatRange(start, endInclusive)

inline fun rangeOrDouble(start: Double, end: Double) = FloatRangeOrFloat(FloatRange(start, end))
inline fun rangeOrDouble(range: FloatRange) = FloatRangeOrFloat(range)
inline fun rangeOrDouble(double: Double) = FloatRangeOrFloat(double = double)
inline fun rangeOrDoubleStart(double: Double) = FloatRangeOrFloat(FloatRange(double, null))
inline fun rangeOrDoubleEnd(double: Double) = FloatRangeOrFloat(FloatRange(null, double))
inline fun Double.asRangeOrDouble() = FloatRangeOrFloat(double = this)
inline fun ClosedFloatingPointRange<Double>.asRangeOrDouble() = FloatRangeOrFloat(FloatRange(start, endInclusive))
