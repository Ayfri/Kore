package io.github.ayfri.kore.helpers.displays.maths.internal

import kotlin.math.*

/** Minimal port of the `org.joml.Math` helpers used by the hand-rolled matrix/quaternion math below. */
internal object JomlMath {
	const val PI_F = PI.toFloat()

	fun sin(rad: Float) = sin(rad.toDouble()).toFloat()
	fun cosFromSin(sin: Float, angle: Float): Float {
		val cos = sqrt(1f - sin * sin)
		var b = (angle + PI_F / 2f) % (PI_F * 2f)
		if (b < 0f) b += PI_F * 2f
		return if (b >= PI_F) -cos else cos
	}

	fun invsqrt(r: Float) = 1f / sqrt(r)
	fun safeAcos(v: Float) = when {
		v < -1f -> PI_F
		v > 1f -> 0f
		else -> acos(v)
	}

	fun atan2(y: Float, x: Float) = atan2(y.toDouble(), x.toDouble()).toFloat()
	fun fma(a: Float, b: Float, c: Float) = a * b + c
}
