package helpers.displays.maths

import org.joml.Math
import org.joml.Matrix3f
import org.joml.Quaternionf

data class GivensPair(val sinHalf: Float, val cosHalf: Float) {
	val cosDouble get() = cosHalf * cosHalf - sinHalf * sinHalf
	val sinDouble get() = 2.0f * sinHalf * cosHalf

	fun negateSin() = GivensPair(-sinHalf, cosHalf)

	fun setQuaternionRealPart(quaternion: Quaternionf): Quaternionf = quaternion.set(sinHalf, 0.0f, 0.0f, cosHalf)

	fun setQuaternionIMagX(quaternion: Quaternionf): Quaternionf = quaternion.set(0.0f, sinHalf, 0.0f, cosHalf)
	fun setQuaternionIMagY(quaternion: Quaternionf): Quaternionf = quaternion.set(0.0f, 0.0f, sinHalf, cosHalf)

	fun setMatrix3fForRotationAboutX(matrix3f: Matrix3f): Matrix3f {
		matrix3f.m01 = 0.0f
		matrix3f.m02 = 0.0f
		matrix3f.m10 = 0.0f
		matrix3f.m20 = 0.0f

		val f = cosDouble
		val g = sinDouble
		matrix3f.m11 = f
		matrix3f.m22 = f
		matrix3f.m12 = g
		matrix3f.m21 = -g
		matrix3f.m00 = 1.0f
		return matrix3f
	}

	fun setMatrix3fForRotationAboutY(matrix3f: Matrix3f): Matrix3f {
		matrix3f.m01 = 0.0f
		matrix3f.m10 = 0.0f
		matrix3f.m12 = 0.0f
		matrix3f.m21 = 0.0f

		val f = cosDouble
		val g = sinDouble
		matrix3f.m00 = f
		matrix3f.m22 = f
		matrix3f.m02 = -g
		matrix3f.m20 = g
		matrix3f.m11 = 1.0f
		return matrix3f
	}

	fun setMatrix3fForRotationAboutZ(matrix3f: Matrix3f): Matrix3f {
		matrix3f.m02 = 0.0f
		matrix3f.m12 = 0.0f
		matrix3f.m20 = 0.0f
		matrix3f.m21 = 0.0f

		val f = cosDouble
		val g = sinDouble
		matrix3f.m00 = f
		matrix3f.m11 = f
		matrix3f.m01 = g
		matrix3f.m10 = -g
		matrix3f.m22 = 1.0f
		return matrix3f
	}

	companion object {
		fun normalize(a: Float, b: Float): GivensPair {
			val f = Math.invsqrt(a * a + b * b)
			return GivensPair(f * a, f * b)
		}

		fun fromAngle(radians: Float): GivensPair {
			val f = Math.sin(radians / 2.0f)
			val g = Math.cosFromSin(f, radians / 2.0f)
			return GivensPair(f, g)
		}
	}
}
