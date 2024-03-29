package io.github.ayfri.kore.arguments.maths

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.enums.Axis
import io.github.ayfri.kore.serializers.ToStringSerializer
import kotlinx.serialization.Serializable

@Serializable(Axes.Companion.AxesRotationSerializer::class)
data class Axes(var x: Boolean = false, var y: Boolean = false, var z: Boolean = false) : Argument {
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

	override fun asString() = toString()

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

		data object AxesRotationSerializer : ToStringSerializer<Axes>()
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
