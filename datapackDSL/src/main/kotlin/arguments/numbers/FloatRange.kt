package arguments.numbers

import kotlinx.serialization.Serializable
import serializers.ToStringSerializer

interface FloatingRange : Range

@Serializable(FloatRange.Companion.FloatRangeSerializer::class)
class FloatRange(val start: Double?, val end: Double?) : FloatingRange {
	override fun toString() = when {
		start == null && end == null -> ""
		start == null -> "..${end?.str}"
		end == null -> "${start.str}.."
		else -> "${start.str}..${end.str}"
	}

	fun asRangeOrDouble() = FloatRangeOrFloat(this)

	companion object {
		data object FloatRangeSerializer : ToStringSerializer<FloatRange>()
	}
}

@Serializable(FloatRangeOrFloat.Companion.FloatRangeOrFloatSerializer::class)
class FloatRangeOrFloat(val range: FloatRange? = null, val double: Double? = null) : FloatingRange {
	override fun toString() = range?.toString() ?: double.toString()

	fun asRange() = range ?: FloatRange(double, double)

	companion object {
		data object FloatRangeOrFloatSerializer : ToStringSerializer<FloatRangeOrFloat>()
	}
}

inline fun range(start: Double, end: Double) = FloatRange(start, end)
inline fun rangeStart(start: Double) = FloatRange(start, null)
inline fun rangeEnd(end: Double) = FloatRange(null, end)
inline fun range(range: ClosedFloatingPointRange<Double>) = FloatRange(range.start, range.endInclusive)
inline fun ClosedFloatingPointRange<Double>.asRange() = FloatRange(start, endInclusive)

inline fun rangeOrDouble(range: FloatRange) = FloatRangeOrFloat(range)
inline fun rangeOrDouble(double: Double) = FloatRangeOrFloat(double = double)
inline fun rangeOrDoubleStart(double: Double) = FloatRangeOrFloat(FloatRange(double, null))
inline fun rangeOrDoubleEnd(double: Double) = FloatRangeOrFloat(FloatRange(null, double))
inline fun Double.asRangeOrDouble() = FloatRangeOrFloat(double = this)
inline fun ClosedFloatingPointRange<Double>.asRangeOrDouble() = FloatRangeOrFloat(FloatRange(start, endInclusive))
