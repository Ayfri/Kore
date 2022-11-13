package arguments

private val Double.str
	get() = when {
		this == toFloat().toDouble() -> this.toInt().toString()
		else -> toString()
	}

class PosNumber(var value: Double, var type: Type = Type.RELATIVE) {
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
		Type.RELATIVE -> "~${value.str}"
		Type.LOCAL -> "^${value.str}"
		Type.WORLD -> value.str
	}
}

fun Double.toPos() = PosNumber(this)
fun Int.toPos() = PosNumber(this.toDouble())
fun pos(value: Double, type: PosNumber.Type = PosNumber.Type.RELATIVE) = PosNumber(value, type)
fun pos(value: Int, type: PosNumber.Type = PosNumber.Type.RELATIVE) = PosNumber(value.toDouble(), type)

class RotNumber(val value: Double, val type: Type = Type.RELATIVE) {
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
		Type.RELATIVE -> "~${value.str}"
		Type.WORLD -> value.str
	}
}

fun Double.toRot() = RotNumber(this)
fun Int.toRot() = RotNumber(this.toDouble())
fun rot(value: Double, type: RotNumber.Type = RotNumber.Type.RELATIVE) = RotNumber(value, type)
fun rot(value: Int, type: RotNumber.Type = RotNumber.Type.RELATIVE) = RotNumber(value.toDouble(), type)
