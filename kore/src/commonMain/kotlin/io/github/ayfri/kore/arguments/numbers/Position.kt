package io.github.ayfri.kore.arguments.numbers

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.absoluteValue
import kotlin.math.floor

/**
 * One axis of a [io.github.ayfri.kore.arguments.maths.Vec3] or other command position component: a numeric value plus
 * whether it is world-absolute, tilde-relative, or caret-local.
 */
@Serializable
data class PosNumber(
	/** Numeric magnitude before any `~` / `^` prefix is applied in command text. */
	var value: Double,
	/** How this component is formatted and interpreted in Minecraft commands. */
	var type: Type = Type.WORLD,
	/**
	 * Whether a world value was built from an `Int`, rendering `100` instead of `100.0`. The game centers integer x/z on
	 * the block (`100` is `100.5`) and block-position arguments reject decimals. Only the `Int` overloads set it, since
	 * Kotlin/JS can't tell `1.0` from `1` at runtime, and arithmetic keeps it while the other operand is whole.
	 */
	@Transient
	var isInteger: Boolean = false,
) : Comparable<PosNumber> {
	@Serializable
	enum class Type(val prefix: String = "") {
		/** Caret/local axis (`^`) for `facing`/`rotation`-relative coordinates. */
		LOCAL("^"),

		/** Tilde-relative offset (`~`) from the execution position. */
		RELATIVE("~"),

		/** Absolute world coordinate (no prefix). */
		WORLD,
	}

	val prefix get() = type.prefix

	val local get() = copy(type = Type.LOCAL)
	val relative get() = copy(type = Type.RELATIVE)
	val world get() = copy(type = Type.WORLD)

	val isLocal get() = type == Type.LOCAL
	val isRelative get() = type == Type.RELATIVE
	val isWorld get() = type == Type.WORLD

	operator fun plus(other: PosNumber) = PosNumber(value + other.value, type, isInteger && other.isInteger)
	operator fun plus(other: Number) = PosNumber(value + other.toDouble(), type, isInteger && other.isWhole)
	operator fun minus(other: PosNumber) = PosNumber(value - other.value, type, isInteger && other.isInteger)
	operator fun minus(other: Number) = PosNumber(value - other.toDouble(), type, isInteger && other.isWhole)
	operator fun times(other: PosNumber) = PosNumber(value * other.value, type, isInteger && other.isInteger)
	operator fun times(other: Number) = PosNumber(value * other.toDouble(), type, isInteger && other.isWhole)
	operator fun div(other: PosNumber) = PosNumber(value / other.value, type, isInteger && other.isInteger)
	operator fun div(other: Number) = PosNumber(value / other.toDouble(), type, isInteger && other.isWhole)
	operator fun rem(other: PosNumber) = PosNumber(value % other.value, type, isInteger && other.isInteger)
	operator fun rem(other: Number) = PosNumber(value % other.toDouble(), type, isInteger && other.isWhole)
	operator fun unaryMinus() = copy(value = -value)
	operator fun unaryPlus() = this

	fun abs() = copy(value = value.absoluteValue)

	override fun compareTo(other: PosNumber) = value.compareTo(other.value)

	override fun toString() = when (type) {
		Type.LOCAL -> "$prefix${value.truncateIfRoundEmptyIfZero}"
		Type.RELATIVE -> "$prefix${value.truncateIfRoundEmptyIfZero}"
		Type.WORLD -> if (isInteger && value % 1.0 == 0.0) value.toLong().toString() else value.toStringWithDecimal
	}

	fun truncate() = copy(value = value.truncated)

	fun toStringTruncatedIfZero() = when (type) {
		Type.LOCAL -> "$prefix${value.truncateIfRoundEmptyIfZero}"
		Type.RELATIVE -> "$prefix${value.truncateIfRoundEmptyIfZero}"
		Type.WORLD -> value.toStringTruncatedIfRound()
	}

	/** Block-position form: world values are floored like Minecraft does, so `-1.5` becomes block `-2`. */
	fun toStringTruncated() = when (type) {
		Type.LOCAL -> "$prefix${value.truncateIfRoundEmptyIfZero}"
		Type.RELATIVE -> "$prefix${value.truncateIfRoundEmptyIfZero}"
		Type.WORLD -> floor(value).toLong().toString()
	}
}

val Number.localPos get() = PosNumber(toDouble(), PosNumber.Type.LOCAL)
val Number.relativePos get() = PosNumber(toDouble(), PosNumber.Type.RELATIVE)

private val Number.isWhole get() = toDouble() % 1.0 == 0.0

/** Parses a world coordinate, `"3"` staying an integer and `"3.0"` a decimal. */
internal fun worldPosOf(text: String) = PosNumber(text.toDouble(), isInteger = text.toIntOrNull() != null)

/** Defaults to [PosNumber.Type.WORLD], unlike [pos] and `vec3(type)` which default to [PosNumber.Type.RELATIVE]. */
val Number.pos get() = PosNumber(toDouble())

/** An integer world coordinate, rendered without decimals so the game centers it on the block. */
val Int.pos get() = PosNumber(toDouble(), isInteger = true)
val Number.worldPos get() = pos
val Int.worldPos get() = pos

/** Defaults to [PosNumber.Type.RELATIVE], unlike [Number.pos] which defaults to [PosNumber.Type.WORLD]. */
fun pos(value: Number = 0, type: PosNumber.Type = PosNumber.Type.RELATIVE) = PosNumber(value.toDouble(), type)
fun pos(value: Int, type: PosNumber.Type = PosNumber.Type.RELATIVE) = PosNumber(value.toDouble(), type, isInteger = true)
