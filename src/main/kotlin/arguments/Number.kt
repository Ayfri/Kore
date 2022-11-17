package arguments

import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive

private val Double.str
	get() = when {
		this == toFloat().toDouble() -> this.toInt().toString()
		else -> toString()
	}

private val Double.strUnlessZero
	get() = when {
		this == 0.0 -> ""
		else -> str
	}

class PosNumber(var value: Double, var type: Type = Type.WORLD) {
	enum class Type {
		RELATIVE,
		LOCAL,
		WORLD
	}
	
	operator fun plus(other: PosNumber) = PosNumber(value + other.value, type)
	operator fun minus(other: PosNumber) = PosNumber(value - other.value, type)
	operator fun times(other: PosNumber) = PosNumber(value * other.value, type)
	operator fun div(other: PosNumber) = PosNumber(value / other.value, type)
	operator fun rem(other: PosNumber) = PosNumber(value % other.value, type)
	operator fun unaryMinus() = PosNumber(-value, type)
	
	val relative get() = PosNumber(value, Type.RELATIVE)
	val local get() = PosNumber(value, Type.LOCAL)
	val world get() = PosNumber(value, Type.WORLD)
	
	override fun toString() = when (type) {
		Type.RELATIVE -> "~${value.strUnlessZero}"
		Type.LOCAL -> "^${value.strUnlessZero}"
		Type.WORLD -> value.str
	}
}

val Double.pos get() = PosNumber(this)
val Double.localPos get() = PosNumber(this, PosNumber.Type.LOCAL)
val Double.relativePos get() = PosNumber(this, PosNumber.Type.RELATIVE)
val Int.pos get() = PosNumber(this.toDouble())
val Int.localPos get() = PosNumber(this.toDouble(), PosNumber.Type.LOCAL)
val Int.relativePos get() = PosNumber(this.toDouble(), PosNumber.Type.RELATIVE)
fun pos(value: Number = 0, type: PosNumber.Type = PosNumber.Type.RELATIVE) = PosNumber(value.toDouble(), type)

class RotNumber(val value: Double, val type: Type = Type.WORLD) {
	enum class Type {
		RELATIVE,
		WORLD
	}
	
	operator fun plus(other: RotNumber) = RotNumber(value + other.value, type)
	operator fun minus(other: RotNumber) = RotNumber(value - other.value, type)
	operator fun times(other: RotNumber) = RotNumber(value * other.value, type)
	operator fun div(other: RotNumber) = RotNumber(value / other.value, type)
	operator fun rem(other: RotNumber) = RotNumber(value % other.value, type)
	
	fun toRelative() = RotNumber(value, Type.RELATIVE)
	fun toWorld() = RotNumber(value, Type.WORLD)
	
	override fun toString() = when (type) {
		Type.RELATIVE -> "~${value.strUnlessZero}"
		Type.WORLD -> value.str
	}
}

val Double.rot get() = RotNumber(this)
val Double.relativeRot get() = RotNumber(this, RotNumber.Type.RELATIVE)
val Int.rot get() = RotNumber(this.toDouble())
val Int.relativeRot get() = RotNumber(this.toDouble(), RotNumber.Type.RELATIVE)
fun rot(value: Number = 0, type: RotNumber.Type = RotNumber.Type.RELATIVE) = RotNumber(value.toDouble(), type)

class XpNumber(val value: Long, val type: ExperienceType = ExperienceType.POINTS) {
	val typeString get() = json.encodeToJsonElement(type).jsonPrimitive.content
	
	operator fun plus(other: XpNumber) = XpNumber(value + other.value, type)
	operator fun minus(other: XpNumber) = XpNumber(value - other.value, type)
	operator fun times(other: XpNumber) = XpNumber(value * other.value, type)
	operator fun div(other: XpNumber) = XpNumber(value / other.value, type)
	operator fun rem(other: XpNumber) = XpNumber(value % other.value, type)
	
	fun toLevels() = XpNumber(value, ExperienceType.LEVELS)
	fun toPoints() = XpNumber(value, ExperienceType.POINTS)
	
	override fun toString() = when (type) {
		ExperienceType.LEVELS -> "${value}L"
		ExperienceType.POINTS -> value.toString()
	}
}

val Int.levels get() = XpNumber(this.toLong(), ExperienceType.LEVELS)
val Int.points get() = XpNumber(this.toLong(), ExperienceType.POINTS)
