package io.github.ayfri.kore.helpers.displays.maths.internal

import kotlin.math.abs

/** Minimal port of `org.joml.Quaternionf`, only the members used by the display transformation math. */
data class Quaternionf(var x: Float = 0f, var y: Float = 0f, var z: Float = 0f, var w: Float = 1f) {
	fun lengthSquared() = JomlMath.fma(x, x, JomlMath.fma(y, y, JomlMath.fma(z, z, w * w)))

	fun set(nx: Float, ny: Float, nz: Float, nw: Float) = apply {
		x = nx
		y = ny
		z = nz
		w = nw
	}

	fun set(other: Quaternionf) = set(other.x, other.y, other.z, other.w)

	fun normalize() = apply {
		val invNorm = JomlMath.invsqrt(JomlMath.fma(x, x, JomlMath.fma(y, y, JomlMath.fma(z, z, w * w))))
		x *= invNorm
		y *= invNorm
		z *= invNorm
		w *= invNorm
	}

	fun add(other: Quaternionf) = apply {
		x += other.x
		y += other.y
		z += other.z
		w += other.w
	}

	fun mul(f: Float) = apply {
		x *= f
		y *= f
		z *= f
		w *= f
	}

	fun mul(q: Quaternionf) = set(
		JomlMath.fma(w, q.x, JomlMath.fma(x, q.w, JomlMath.fma(y, q.z, -z * q.y))),
		JomlMath.fma(w, q.y, JomlMath.fma(-x, q.z, JomlMath.fma(y, q.w, z * q.x))),
		JomlMath.fma(w, q.z, JomlMath.fma(x, q.y, JomlMath.fma(-y, q.x, z * q.w))),
		JomlMath.fma(w, q.w, JomlMath.fma(-x, q.x, JomlMath.fma(-y, q.y, -z * q.z))),
	)

	fun invert() = apply {
		val invNorm = 1f / JomlMath.fma(x, x, JomlMath.fma(y, y, JomlMath.fma(z, z, w * w)))
		val nx = -x * invNorm
		val ny = -y * invNorm
		val nz = -z * invNorm
		val nw = w * invNorm
		set(nx, ny, nz, nw)
	}

	fun conjugate() = set(-x, -y, -z, w)

	fun rotateXYZ(angleX: Float, angleY: Float, angleZ: Float) = apply {
		val sx = JomlMath.sin(angleX * 0.5f)
		val cx = JomlMath.cosFromSin(sx, angleX * 0.5f)
		val sy = JomlMath.sin(angleY * 0.5f)
		val cy = JomlMath.cosFromSin(sy, angleY * 0.5f)
		val sz = JomlMath.sin(angleZ * 0.5f)
		val cz = JomlMath.cosFromSin(sz, angleZ * 0.5f)

		val cycz = cy * cz
		val sysz = sy * sz
		val sycz = sy * cz
		val cysz = cy * sz
		val nw = cx * cycz - sx * sysz
		val nx = sx * cycz + cx * sysz
		val ny = cx * sycz - sx * cysz
		val nz = cx * cysz + sx * sycz

		set(
			JomlMath.fma(w, nx, JomlMath.fma(x, nw, JomlMath.fma(y, nz, -z * ny))),
			JomlMath.fma(w, ny, JomlMath.fma(-x, nz, JomlMath.fma(y, nw, z * nx))),
			JomlMath.fma(w, nz, JomlMath.fma(x, ny, JomlMath.fma(-y, nx, z * nw))),
			JomlMath.fma(w, nw, JomlMath.fma(-x, nx, JomlMath.fma(-y, ny, -z * nz))),
		)
	}

	fun slerp(target: Quaternionf, alpha: Float) = apply {
		val cosom = JomlMath.fma(x, target.x, JomlMath.fma(y, target.y, JomlMath.fma(z, target.z, w * target.w)))
		val absCosom = abs(cosom)
		var scale0: Float
		var scale1: Float
		if (1f - absCosom > 1e-6f) {
			val sinSqr = 1f - absCosom * absCosom
			val sinom = JomlMath.invsqrt(sinSqr)
			val omega = JomlMath.atan2(sinSqr * sinom, absCosom)
			scale0 = JomlMath.sin((1f - alpha) * omega) * sinom
			scale1 = JomlMath.sin(alpha * omega) * sinom
		} else {
			scale0 = 1f - alpha
			scale1 = alpha
		}
		scale1 = if (cosom >= 0f) scale1 else -scale1
		set(
			JomlMath.fma(scale0, x, scale1 * target.x),
			JomlMath.fma(scale0, y, scale1 * target.y),
			JomlMath.fma(scale0, z, scale1 * target.z),
			JomlMath.fma(scale0, w, scale1 * target.w),
		)
	}
}
