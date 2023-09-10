package io.github.ayfri.kore.helpers.displays.maths

import kotlin.math.acos

const val PI_f = Math.PI.toFloat()

fun invsqrt(x: Float): Float {
	val xhalf = 0.5f * x
	var i = java.lang.Float.floatToIntBits(x)
	i = 0x5f3759df - (i shr 1)
	var y = java.lang.Float.intBitsToFloat(i)
	y *= (1.5f - xhalf * y * y)
	return y
}

fun safeAcos(v: Float) = if (v < -1.0f) PI_f else if (v > +1.0f) 0.0f else acos(v.toDouble()).toFloat()
