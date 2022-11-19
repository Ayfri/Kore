package arguments

import functions.Function
import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer
import serializers.ToStringSerializer

@Serializable(Axis.Companion.AxisSerializer::class)
enum class Axis {
	X,
	Y,
	Z;
	
	companion object {
		val values = values()
		
		object AxisSerializer : LowercaseSerializer<Axis>(values)
	}
}

@Serializable(Axes.Companion.AxesRotationSerializer::class)
class Axes(var x: Boolean = false, var y: Boolean = false, var z: Boolean = false) {
	override fun toString() = when {
		x && y && z -> "xyz"
		x && y -> "xy"
		x && z -> "xz"
		y && z -> "yz"
		x -> "x"
		y -> "y"
		z -> "z"
		else -> ""
	}
	
	fun x() = apply { x = true }
	fun y() = apply { y = true }
	fun z() = apply { z = true }
	
	companion object {
		val X = Axes(x = true)
		val Y = Axes(y = true)
		val Z = Axes(z = true)
		val XY = Axes(x = true, y = true)
		val XZ = Axes(x = true, z = true)
		val YZ = Axes(y = true, z = true)
		val XYZ = Axes(x = true, y = true, z = true)
		
		object AxesRotationSerializer : ToStringSerializer<Axes>()
	}
}

fun Function.axes(init: Axes.() -> Unit) = Axes().apply(init)
fun Function.axes(x: Boolean = false, y: Boolean = false, z: Boolean = false) = Axes(x, y, z)
fun Function.x() = Axis.X
fun Function.y() = Axis.Y
fun Function.z() = Axis.Z
fun Function.xy() = Axes.XY
fun Function.xz() = Axes.XZ
fun Function.yz() = Axes.YZ
fun Function.xyz() = Axes.XYZ
