package io.github.ayfri.kore.helpers.displays.maths

import io.github.ayfri.kore.helpers.displays.maths.internal.Quaternionf
import kotlin.math.cos
import kotlin.math.sin

class Quaternion(x: Float = 0f, y: Float = 0f, z: Float = 0f, w: Float = 1f) {
	constructor(x: Number, y: Number, z: Number, w: Number) : this(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat())
	constructor(vector: Vec3f, scalar: Float) : this(vector.x, vector.y, vector.z, scalar)
	constructor(quaternion: Quaternion) : this(quaternion.x, quaternion.y, quaternion.z, quaternion.w)
	constructor(quaternion: Quaternionf) : this() {
		this.quaternion = quaternion
	}

	var quaternion = Quaternionf(x, y, z, w)

	val x get() = quaternion.x
	val y get() = quaternion.y
	val z get() = quaternion.z
	val w get() = quaternion.w

	val lengthSquared get() = quaternion.lengthSquared()
	val length get() = quaternion.x * quaternion.x + quaternion.y * quaternion.y + quaternion.z * quaternion.z + quaternion.w * quaternion.w
	val conjugate get() = Quaternion(quaternion.conjugate())

	operator fun unaryMinus() = Quaternion(-x, -y, -z, -w)
	operator fun plus(other: Quaternion) = apply { quaternion.add(other.quaternion) }
	operator fun minus(other: Quaternion) = apply {
		quaternion.add(other.quaternion.apply {
			x = -x
			y = -y
			z = -z
			w = -w
		})
	}

	operator fun times(scalar: Float) = apply { quaternion.mul(scalar) }
	operator fun times(other: Quaternion) = apply { quaternion.mul(other.quaternion) }
	operator fun div(scalar: Float) = apply { quaternion.mul(1 / scalar) }

	operator fun component1() = x
	operator fun component2() = y
	operator fun component3() = z
	operator fun component4() = w

	fun copy() = Quaternion(this)

	fun invert() = apply { quaternion.invert() }
	fun normalize() = apply { quaternion.normalize() }

	fun slerp(other: Quaternion, t: Float) = apply { quaternion.slerp(other.quaternion, t) }

	fun toAxisAngle() = AxisAngle(this)

	fun toRotationMatrix() = Matrix().apply {
		matrix.set(quaternion)
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (this::class != other?.let { it::class }) return false

		other as Quaternion

		return quaternion == other.quaternion
	}

	override fun hashCode() = quaternion.hashCode()

	override fun toString() = "Quaternion(quaternion=$quaternion)"

	companion object {
		val IDENTITY = Quaternion()

		fun fromAxisAngle(axis: Vec3f, angle: Float): Quaternion {
			val halfSin = sin(angle * 0.5f)
			val halfCos = cos(angle * 0.5f)
			return Quaternion(axis.x * halfSin, axis.y * halfSin, axis.z * halfSin, halfCos)
		}
		fun fromAxisAngle(axisAngle: AxisAngle) = fromAxisAngle(axisAngle.axis, axisAngle.angle)
		fun fromEulerAngles(x: Float, y: Float, z: Float) = Quaternion(Quaternionf().rotateXYZ(x, y, z))
		fun fromEulerAngles(eulerAngles: Vec3f) = fromEulerAngles(eulerAngles.x, eulerAngles.y, eulerAngles.z)

		fun slerp(a: Quaternion, b: Quaternion, t: Float) =
			Quaternion(a.quaternion.copy().slerp(b.quaternion, t))
	}
}
