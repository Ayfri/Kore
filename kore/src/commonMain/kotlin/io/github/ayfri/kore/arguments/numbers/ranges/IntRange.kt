package io.github.ayfri.kore.arguments.numbers.ranges

import io.github.ayfri.kore.arguments.Argument
import kotlinx.serialization.Serializable
import kotlin.ranges.IntRange as KotlinIntRange

interface IntegerRange : Range

@Serializable(with = Argument.ArgumentSerializer::class)
data class IntRange(val start: Int?, val end: Int?) : IntegerRange {
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
data class IntRangeOrInt(val range: IntRange? = null, val int: Int? = null) : IntegerRange {
	override fun asString() = range?.toString() ?: int.toString()

	override fun toString() = asString()

	fun asRange() = range ?: IntRange(int, int)
}

fun range(start: Int, end: Int) = IntRange(start, end)
fun rangeStart(start: Int) = IntRange(start, null)
fun rangeEnd(end: Int) = IntRange(null, end)
fun range(range: KotlinIntRange) = IntRange(range.first, range.last)
fun KotlinIntRange.asRange() = IntRange(first, last)

fun rangeOrInt(range: KotlinIntRange) = IntRangeOrInt(IntRange(range.first, range.last))
fun rangeOrInt(range: IntRange) = IntRangeOrInt(range)
fun rangeOrInt(int: Int) = IntRangeOrInt(int = int)
fun rangeOrIntStart(int: Int) = IntRangeOrInt(IntRange(int, null))
fun rangeOrIntEnd(int: Int) = IntRangeOrInt(IntRange(null, int))
fun Int.asRangeOrInt() = IntRangeOrInt(int = this)
fun Int.asStartRangeOrInt() = IntRangeOrInt(IntRange(this, null))
fun Int.asEndRangeOrInt() = IntRangeOrInt(IntRange(null, this))
fun KotlinIntRange.asRangeOrInt() = IntRangeOrInt(IntRange(first, last))
