package arguments

import kotlinx.serialization.Serializable
import serializers.ToStringSerializer

@Serializable(Range.Companion.RangeSerializer::class)
class Range(val start: Int?, val end: Int?) {
	override fun toString() = when {
		start == null && end == null -> ""
		start == null -> "..$end"
		end == null -> "$start.."
		else -> "$start..$end"
	}
	
	fun asRangeOrInt() = RangeOrInt(this)
	
	companion object {
		object RangeSerializer : ToStringSerializer<Range>()
	}
}

@Serializable(RangeOrInt.Companion.RangeOrIntSerializer::class)
class RangeOrInt(val range: Range? = null, val int: Int? = null) {
	override fun toString() = range?.toString() ?: int.toString()
	
	fun asRange() = range ?: Range(int, int)
	
	companion object {
		object RangeOrIntSerializer : ToStringSerializer<RangeOrInt>()
	}
}

inline fun range(start: Int, end: Int) = Range(start, end)
inline fun rangeStart(start: Int) = Range(start, null)
inline fun rangeEnd(end: Int) = Range(null, end)
inline fun range(range: IntRange) = Range(range.first, range.last)
inline fun IntRange.asRange() = Range(first, last)

inline fun rangeOrInt(range: Range) = RangeOrInt(range)
inline fun rangeOrInt(int: Int) = RangeOrInt(int = int)
inline fun Int.asRangeOrInt() = RangeOrInt(int = this)
inline fun IntRange.asRangeOrInt() = RangeOrInt(Range(first, last))
