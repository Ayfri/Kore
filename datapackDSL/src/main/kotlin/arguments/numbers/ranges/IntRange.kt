package arguments.numbers.ranges

import arguments.Argument
import kotlinx.serialization.Serializable
import kotlin.ranges.IntRange as KotlinIntRange

interface IntegerRange : Range

@Serializable(with = Argument.ArgumentSerializer::class)
class IntRange(val start: Int?, val end: Int?) : IntegerRange {
	override fun asString() = when {
		start == null && end == null -> ""
		start == null -> "..$end"
		end == null -> "$start.."
		else -> "$start..$end"
	}

	override fun toString() = asString()

	fun asRangeOrInt() = IntRangeOrInt(this)
}

@Serializable(with = Argument.ArgumentSerializer::class)
class IntRangeOrInt(val range: IntRange? = null, val int: Int? = null) : IntegerRange {
	override fun asString() = range?.toString() ?: int.toString()

	override fun toString() = asString()

	fun asRange() = range ?: IntRange(int, int)
}

inline fun range(start: Int, end: Int) = IntRange(start, end)
inline fun rangeStart(start: Int) = IntRange(start, null)
inline fun rangeEnd(end: Int) = IntRange(null, end)
inline fun range(range: KotlinIntRange) = IntRange(range.first, range.last)
inline fun KotlinIntRange.asRange() = IntRange(first, last)

inline fun rangeOrInt(range: KotlinIntRange) = IntRangeOrInt(IntRange(range.first, range.last))
inline fun rangeOrInt(range: IntRange) = IntRangeOrInt(range)
inline fun rangeOrInt(int: Int) = IntRangeOrInt(int = int)
inline fun rangeOrIntStart(int: Int) = IntRangeOrInt(IntRange(int, null))
inline fun rangeOrIntEnd(int: Int) = IntRangeOrInt(IntRange(null, int))
inline fun Int.asRangeOrInt() = IntRangeOrInt(int = this)
inline fun Int.asStartRangeOrInt() = IntRangeOrInt(IntRange(this, null))
inline fun Int.asEndRangeOrInt() = IntRangeOrInt(IntRange(null, this))
inline fun KotlinIntRange.asRangeOrInt() = IntRangeOrInt(IntRange(first, last))
