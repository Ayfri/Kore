package io.github.ayfri.kore.helpers.displays.maths

import org.joml.AxisAngle4f

class AxisAngle(
	angle: Float = 0f,
	x: Float = 0f,
	y: Float = 0f,
	z: Float = 1f,
) {
	constructor(quaternion: Quaternion) : this() {
		axisAngle = AxisAngle4f(quaternion.quaternion)
	}

	constructor(axisAngle: AxisAngle4f) : this() {
		this.axisAngle = axisAngle
	}

	private var axisAngle = AxisAngle4f(x, y, z, angle)

	val axis get() = Vec3f(axisAngle.x, axisAngle.y, axisAngle.z)
	val angle get() = axisAngle.angle

	operator fun component1() = axis
	operator fun component2() = angle

	fun set(quaternion: Quaternion) = apply {
		axisAngle.set(quaternion.quaternion)
	}

	fun set(m: Matrix) = apply {
		axisAngle.set(m.matrix)
	}

	fun normalize() = apply {
		axisAngle.normalize()
	}

	fun toQuaternion() = Quaternion.fromAxisAngle(axis, angle)

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as AxisAngle

		return axisAngle == other.axisAngle
	}

	override fun hashCode() = axisAngle.hashCode()

	override fun toString() = "AxisAngle(axisAngle=$axisAngle)"
}
