package io.github.ayfri.kore.helpers.displays.maths.internal

import kotlin.math.abs

/** Minimal port of `org.joml.AxisAngle4f`, only the members used by the display transformation math. */
data class AxisAngle4f(var x: Float = 0f, var y: Float = 0f, var z: Float = 1f, var angle: Float = 0f) {
	constructor(quaternion: Quaternionf) : this() {
		set(quaternion)
	}

	fun set(q: Quaternionf) = apply {
		val acos = JomlMath.safeAcos(q.w)
		val invSqrt = JomlMath.invsqrt(1f - q.w * q.w)
		if (invSqrt.isInfinite()) {
			x = 0f; y = 0f; z = 1f
		} else {
			x = q.x * invSqrt
			y = q.y * invSqrt
			z = q.z * invSqrt
		}
		angle = acos + acos
	}

	fun set(m: Matrix4f) = apply {
		var nm00 = m.m00
		var nm01 = m.m01
		var nm02 = m.m02
		var nm10 = m.m10
		var nm11 = m.m11
		var nm12 = m.m12
		var nm20 = m.m20
		var nm21 = m.m21
		var nm22 = m.m22
		val lenX = JomlMath.invsqrt(m.m00 * m.m00 + m.m01 * m.m01 + m.m02 * m.m02)
		val lenY = JomlMath.invsqrt(m.m10 * m.m10 + m.m11 * m.m11 + m.m12 * m.m12)
		val lenZ = JomlMath.invsqrt(m.m20 * m.m20 + m.m21 * m.m21 + m.m22 * m.m22)
		nm00 *= lenX; nm01 *= lenX; nm02 *= lenX
		nm10 *= lenY; nm11 *= lenY; nm12 *= lenY
		nm20 *= lenZ; nm21 *= lenZ; nm22 *= lenZ

		val epsilon = 1e-4f
		val epsilon2 = 1e-3f
		if (abs(nm10 - nm01) < epsilon && abs(nm20 - nm02) < epsilon && abs(nm21 - nm12) < epsilon) {
			if (
				abs(nm10 + nm01) < epsilon2 && abs(nm20 + nm02) < epsilon2 && abs(nm21 + nm12) < epsilon2 &&
				abs(nm00 + nm11 + nm22 - 3) < epsilon2
			) {
				x = 0f; y = 0f; z = 1f; angle = 0f
				return@apply
			}
			angle = JomlMath.PI_F
			val xx = (nm00 + 1) / 2
			val yy = (nm11 + 1) / 2
			val zz = (nm22 + 1) / 2
			val xy = (nm10 + nm01) / 4
			val xz = (nm20 + nm02) / 4
			val yz = (nm21 + nm12) / 4
			when {
				xx > yy && xx > zz -> {
					x = kotlin.math.sqrt(xx)
					val invX = 1f / x
					y = xy * invX
					z = xz * invX
				}

				yy > zz -> {
					y = kotlin.math.sqrt(yy)
					val invZ = 1f / z
					x = xy * invZ
					z = yz * invZ
				}

				else -> {
					z = kotlin.math.sqrt(zz)
					val invY = 1f / y
					x = xz * invY
					y = yz * invY
				}
			}
			return@apply
		}

		val s =
			JomlMath.invsqrt((nm12 - nm21) * (nm12 - nm21) + (nm20 - nm02) * (nm20 - nm02) + (nm01 - nm10) * (nm01 - nm10))
		angle = JomlMath.safeAcos((nm00 + nm11 + nm22 - 1) / 2)
		x = (nm12 - nm21) * s
		y = (nm20 - nm02) * s
		z = (nm01 - nm10) * s
	}

	fun normalize() = apply {
		val invLength = JomlMath.invsqrt(x * x + y * y + z * z)
		x *= invLength
		y *= invLength
		z *= invLength
	}
}
