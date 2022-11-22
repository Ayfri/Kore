package arguments

import arguments.enums.Axis
import kotlinx.serialization.Serializable
import serializers.ToStringSerializer

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

fun axes(init: Axes.() -> Unit) = Axes().apply(init)
fun axes(x: Boolean = false, y: Boolean = false, z: Boolean = false) = Axes(x, y, z)
fun x() = Axis.X
fun y() = Axis.Y
fun z() = Axis.Z
fun xy() = Axes.XY
fun xz() = Axes.XZ
fun yz() = Axes.YZ
fun xyz() = Axes.XYZ
