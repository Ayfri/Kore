package arguments.numbers

class PosNumber(var value: Double, var type: Type = Type.WORLD) {
	enum class Type {
		RELATIVE,
		LOCAL,
		WORLD
	}
	
	operator fun plus(other: PosNumber) = PosNumber(value + other.value, type)
	operator fun plus(other: Number) = PosNumber(value + other.toDouble(), type)
	operator fun minus(other: PosNumber) = PosNumber(value - other.value, type)
	operator fun minus(other: Number) = PosNumber(value - other.toDouble(), type)
	operator fun times(other: PosNumber) = PosNumber(value * other.value, type)
	operator fun times(other: Number) = PosNumber(value * other.toDouble(), type)
	operator fun div(other: PosNumber) = PosNumber(value / other.value, type)
	operator fun div(other: Number) = PosNumber(value / other.toDouble(), type)
	operator fun rem(other: PosNumber) = PosNumber(value % other.value, type)
	operator fun rem(other: Number) = PosNumber(value % other.toDouble(), type)
	operator fun unaryMinus() = PosNumber(-value, type)
	operator fun unaryPlus() = PosNumber(+value, type)
	
	val relative get() = PosNumber(value, Type.RELATIVE)
	val local get() = PosNumber(value, Type.LOCAL)
	val world get() = PosNumber(value, Type.WORLD)
	
	override fun toString() = when (type) {
		Type.RELATIVE -> "~${value.strUnlessZero}"
		Type.LOCAL -> "^${value.strUnlessZero}"
		Type.WORLD -> value.str
	}
}

val Number.pos get() = PosNumber(toDouble())
val Number.localPos get() = PosNumber(toDouble(), PosNumber.Type.LOCAL)
val Number.relativePos get() = PosNumber(toDouble(), PosNumber.Type.RELATIVE)
fun pos(value: Number = 0, type: PosNumber.Type = PosNumber.Type.RELATIVE) = PosNumber(value.toDouble(), type)
