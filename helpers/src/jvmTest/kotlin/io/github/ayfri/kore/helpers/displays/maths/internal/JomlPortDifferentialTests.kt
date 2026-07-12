package io.github.ayfri.kore.helpers.displays.maths.internal

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.shouldBe
import kotlin.math.PI
import kotlin.math.sqrt
import org.joml.AxisAngle4f as JAxisAngle4f
import org.joml.Matrix3f as JMatrix3f
import org.joml.Matrix4f as JMatrix4f
import org.joml.Quaternionf as JQuaternionf

class JomlPortDifferentialTests : FunSpec({
	val eps = 1e-4f

	fun Float.approx(expected: Float) = this shouldBe (expected plusOrMinus eps)

	fun Quaternionf.approx(expected: JQuaternionf) {
		x.approx(expected.x)
		y.approx(expected.y)
		z.approx(expected.z)
		w.approx(expected.w)
	}

	fun Matrix3f.approx(expected: JMatrix3f) {
		m00.approx(expected.m00()); m01.approx(expected.m01()); m02.approx(expected.m02())
		m10.approx(expected.m10()); m11.approx(expected.m11()); m12.approx(expected.m12())
		m20.approx(expected.m20()); m21.approx(expected.m21()); m22.approx(expected.m22())
	}

	fun Matrix4f.approx(expected: JMatrix4f) {
		val actualArr = FloatArray(16).also { get(it) }
		val expectedArr = FloatArray(16).also { expected.get(it) }
		actualArr.indices.forEach { i -> actualArr[i].approx(expectedArr[i]) }
	}

	fun AxisAngle4f.approx(expected: JAxisAngle4f) {
		x.approx(expected.x)
		y.approx(expected.y)
		z.approx(expected.z)
		angle.approx(expected.angle)
	}

	fun Matrix4f.toJoml(): JMatrix4f {
		val arr = FloatArray(16).also { get(it) }
		return JMatrix4f().set(arr)
	}

	context("Quaternionf") {
		test("lengthSquared matches") {
			val q = Quaternionf(0.1f, 0.2f, 0.3f, 0.9f)
			val jq = JQuaternionf(0.1f, 0.2f, 0.3f, 0.9f)
			q.lengthSquared().approx(jq.lengthSquared())
		}

		test("normalize matches") {
			val q = Quaternionf(0.4f, -0.3f, 0.2f, 0.8f).normalize()
			val jq = JQuaternionf(0.4f, -0.3f, 0.2f, 0.8f).normalize()
			q.approx(jq)
		}

		test("invert matches for a non-unit quaternion") {
			val q = Quaternionf(0.4f, -0.3f, 0.2f, 0.8f).invert()
			val jq = JQuaternionf(0.4f, -0.3f, 0.2f, 0.8f).invert()
			q.approx(jq)
		}

		test("conjugate matches") {
			val q = Quaternionf(0.1f, 0.2f, 0.3f, 0.9f).conjugate()
			val jq = JQuaternionf(0.1f, 0.2f, 0.3f, 0.9f).conjugate()
			q.approx(jq)
		}

		test("add matches") {
			val q = Quaternionf(0.1f, 0.2f, 0.3f, 0.9f).add(Quaternionf(0.5f, -0.1f, 0.4f, 0.2f))
			val jq = JQuaternionf(0.1f, 0.2f, 0.3f, 0.9f).add(JQuaternionf(0.5f, -0.1f, 0.4f, 0.2f))
			q.approx(jq)
		}

		test("mul by scalar matches") {
			val q = Quaternionf(0.1f, 0.2f, 0.3f, 0.9f).mul(2.5f)
			val jq = JQuaternionf(0.1f, 0.2f, 0.3f, 0.9f).mul(2.5f)
			q.approx(jq)
		}

		test("mul by quaternion (Hamilton product) matches") {
			val q = Quaternionf(0.1f, 0.2f, 0.3f, 0.9f).mul(Quaternionf(0.5f, -0.1f, 0.4f, 0.2f))
			val jq = JQuaternionf(0.1f, 0.2f, 0.3f, 0.9f).mul(JQuaternionf(0.5f, -0.1f, 0.4f, 0.2f))
			q.approx(jq)
		}

		test("rotateXYZ from identity matches") {
			val q = Quaternionf().rotateXYZ(0.3f, -0.5f, 1.2f)
			val jq = JQuaternionf().rotateXYZ(0.3f, -0.5f, 1.2f)
			q.approx(jq)
		}

		test("rotateXYZ from a non-identity quaternion matches") {
			val q = Quaternionf(0.1f, 0.2f, 0.3f, 0.9f).normalize().rotateXYZ(0.3f, -0.5f, 1.2f)
			val jq = JQuaternionf(0.1f, 0.2f, 0.3f, 0.9f).normalize().rotateXYZ(0.3f, -0.5f, 1.2f)
			q.approx(jq)
		}

		test("slerp matches across the interpolation range with a positive dot product") {
			val a = Quaternionf(0.1f, 0.0f, 0.0f, 0.995f).normalize()
			val b = Quaternionf(0.0f, 0.2f, 0.05f, 0.98f).normalize()
			val ja = JQuaternionf(0.1f, 0.0f, 0.0f, 0.995f).normalize()
			val jb = JQuaternionf(0.0f, 0.2f, 0.05f, 0.98f).normalize()

			for (t in listOf(0f, 0.25f, 0.5f, 0.75f, 1f)) {
				a.copy().slerp(b, t).approx(JQuaternionf(ja).slerp(jb, t))
			}
		}

		test("slerp matches when the dot product is negative (long way around)") {
			val a = Quaternionf(0.1f, 0.0f, 0.0f, 0.995f).normalize()
			val b = Quaternionf(-0.05f, -0.2f, -0.1f, -0.97f).normalize()
			val ja = JQuaternionf(0.1f, 0.0f, 0.0f, 0.995f).normalize()
			val jb = JQuaternionf(-0.05f, -0.2f, -0.1f, -0.97f).normalize()

			for (t in listOf(0f, 0.25f, 0.5f, 0.75f, 1f)) {
				a.copy().slerp(b, t).approx(JQuaternionf(ja).slerp(jb, t))
			}
		}

		test("slerp matches for near-identical quaternions (linear-interpolation branch)") {
			val a = Quaternionf(0.1f, 0.2f, 0.3f, 0.9f).normalize()
			val b = Quaternionf(0.1000001f, 0.2000001f, 0.3000001f, 0.9000001f).normalize()
			val ja = JQuaternionf(0.1f, 0.2f, 0.3f, 0.9f).normalize()
			val jb = JQuaternionf(0.1000001f, 0.2000001f, 0.3000001f, 0.9000001f).normalize()

			a.copy().slerp(b, 0.5f).approx(JQuaternionf(ja).slerp(jb, 0.5f))
		}
	}

	context("Matrix4f") {
		val startArr = floatArrayOf(
			2f, 0.1f, 0f, 0f,
			0f, 1.5f, 0.2f, 0f,
			0.3f, 0f, 0.8f, 0f,
			1f, 2f, 3f, 1f,
		)

		fun freshPair(): Pair<Matrix4f, JMatrix4f> =
			Matrix4f().setTransposed(startArr) to JMatrix4f().setTransposed(startArr)

		test("identity matches") {
			Matrix4f().identity().approx(JMatrix4f().identity())
		}

		test("setTransposed/get round-trip matches") {
			val (m, jm) = freshPair()
			m.approx(jm)
		}

		test("scale(x, y, z) matches") {
			val (m, jm) = freshPair()
			m.scale(2f, 0.5f, 3f).approx(jm.scale(2f, 0.5f, 3f))
		}

		test("uniform scale matches") {
			val (m, jm) = freshPair()
			m.scale(1.5f).approx(jm.scale(1.5f))
		}

		test("translate matches") {
			val (m, jm) = freshPair()
			m.translate(1.2f, -3.4f, 5.6f).approx(jm.translate(1.2f, -3.4f, 5.6f))
		}

		test("rotate about an axis-aligned unit axis matches") {
			val (m, jm) = freshPair()
			m.rotate(0.9f, 0f, 1f, 0f).approx(jm.rotate(0.9f, 0f, 1f, 0f))
		}

		test("rotate about an arbitrary unit axis matches") {
			val axis = floatArrayOf(1f, 1f, 1f).map { it / sqrt(3f) }
			val (m, jm) = freshPair()
			m.rotate(1.3f, axis[0], axis[1], axis[2]).approx(jm.rotate(1.3f, axis[0], axis[1], axis[2]))
		}

		test("mul matches for a composed TRS chain") {
			val (m, jm) = freshPair()
			val (other, jOther) = freshPair()
			other.translate(0.5f, 0.5f, 0.5f).rotate(0.4f, 0f, 0f, 1f)
			jOther.translate(0.5f, 0.5f, 0.5f).rotate(0.4f, 0f, 0f, 1f)

			m.mul(other).approx(jm.mul(jOther))
		}

		test("invert matches for a composed TRS matrix") {
			val (m, jm) = freshPair()
			m.translate(1f, 2f, 3f).rotate(0.7f, 0f, 1f, 0f).scale(1.5f, 1.5f, 1.5f)
			jm.translate(1f, 2f, 3f).rotate(0.7f, 0f, 1f, 0f).scale(1.5f, 1.5f, 1.5f)

			m.invert().approx(jm.invert())
		}

		test("transpose matches") {
			val (m, jm) = freshPair()
			m.transpose().approx(jm.transpose())
		}

		test("add matches") {
			val (m, jm) = freshPair()
			val (other, jOther) = freshPair()
			m.add(other).approx(jm.add(jOther))
		}

		test("sub matches") {
			val (m, jm) = freshPair()
			val (other, jOther) = freshPair()
			m.sub(other).approx(jm.sub(jOther))
		}

		test("set(Quaternionf) rotation matrix matches") {
			val q = Quaternionf(0.1f, 0.2f, 0.3f, 0.9f).normalize()
			val jq = JQuaternionf(0.1f, 0.2f, 0.3f, 0.9f).normalize()
			Matrix4f().set(q).approx(JMatrix4f().set(jq))
		}
	}

	context("Matrix3f") {
		test("mul matches") {
			val a = Matrix3f(1f, 0.2f, 0f, 0.1f, 1f, 0.3f, 0f, 0f, 1f)
			val b = Matrix3f(0.9f, 0f, 0.1f, 0f, 1.1f, 0f, 0.2f, 0f)
			val ja = JMatrix3f(1f, 0.2f, 0f, 0.1f, 1f, 0.3f, 0f, 0f, 1f)
			val jb = JMatrix3f(0.9f, 0f, 0.1f, 0f, 1.1f, 0f, 0.2f, 0f, 1f)

			a.mul(b).approx(ja.mul(jb))
		}

		test("transpose matches") {
			val a = Matrix3f(m01 = 0.2f, m10 = 0.1f, m12 = 0.3f)
			val ja = JMatrix3f(1f, 0.2f, 0f, 0.1f, 1f, 0.3f, 0f, 0f, 1f)
			a.transpose().approx(ja.transpose())
		}

		test("rotate(quaternion) from identity matches") {
			val q = Quaternionf(0.1f, 0.2f, 0.3f, 0.9f).normalize()
			val jq = JQuaternionf(0.1f, 0.2f, 0.3f, 0.9f).normalize()
			Matrix3f().rotate(q).approx(JMatrix3f().rotate(jq))
		}

		test("rotate(quaternion) from a non-identity matrix matches") {
			val q = Quaternionf(0.1f, 0.2f, 0.3f, 0.9f).normalize()
			val jq = JQuaternionf(0.1f, 0.2f, 0.3f, 0.9f).normalize()
			val a = Matrix3f(m01 = 0.2f, m10 = 0.1f, m12 = 0.3f)
			val ja = JMatrix3f(1f, 0.2f, 0f, 0.1f, 1f, 0.3f, 0f, 0f, 1f)

			a.rotate(q).approx(ja.rotate(jq))
		}

		test("constructor from Matrix4f copies the upper-left 3x3") {
			val m = Matrix4f().translate(1f, 2f, 3f).rotate(0.7f, 0f, 1f, 0f).scale(1.5f, 1.5f, 1.5f)
			val jm = m.toJoml()

			Matrix3f(m).approx(JMatrix3f(jm))
		}
	}

	context("AxisAngle4f") {
		test("from an identity quaternion matches") {
			AxisAngle4f(Quaternionf()).approx(JAxisAngle4f(JQuaternionf()))
		}

		test("from an arbitrary quaternion matches") {
			val q = Quaternionf(0.1f, 0.2f, 0.3f, 0.9f).normalize()
			val jq = JQuaternionf(0.1f, 0.2f, 0.3f, 0.9f).normalize()
			AxisAngle4f(q).approx(JAxisAngle4f(jq))
		}

		test("normalize matches") {
			AxisAngle4f(2f, 4f, 4f, 1f).normalize().approx(JAxisAngle4f(1f, 2f, 4f, 4f).normalize())
		}

		test("set(Matrix4f) matches for the identity matrix") {
			val m = Matrix4f()
			val jm = m.toJoml()
			AxisAngle4f().set(m).approx(JAxisAngle4f().set(jm))
		}

		test("set(Matrix4f) matches for a general (non-180-degree) rotation") {
			val m = Matrix4f().rotate(1.0f, 0.4f, 0.6f, sqrt(1f - 0.4f * 0.4f - 0.6f * 0.6f))
			val jm = m.toJoml()
			AxisAngle4f().set(m).approx(JAxisAngle4f().set(jm))
		}

		test("set(Matrix4f) matches at a near-180-degree rotation dominated by the x axis") {
			val m = Matrix4f().rotate(PI.toFloat() - 0.0001f, 1f, 0f, 0f)
			val jm = m.toJoml()
			AxisAngle4f().set(m).approx(JAxisAngle4f().set(jm))
		}

		test("set(Matrix4f) matches at a near-180-degree rotation dominated by the y axis") {
			val m = Matrix4f().rotate(PI.toFloat() - 0.0001f, 0f, 1f, 0f)
			val jm = m.toJoml()
			AxisAngle4f().set(m).approx(JAxisAngle4f().set(jm))
		}

		test("set(Matrix4f) matches at a near-180-degree rotation dominated by the z axis") {
			val m = Matrix4f().rotate(PI.toFloat() - 0.0001f, 0f, 0f, 1f)
			val jm = m.toJoml()
			AxisAngle4f().set(m).approx(JAxisAngle4f().set(jm))
		}

		test("set(Matrix4f) matches at exactly 180 degrees about a diagonal axis") {
			val axis = floatArrayOf(1f, 1f, 1f).map { it / sqrt(3f) }
			val m = Matrix4f().rotate(PI.toFloat(), axis[0], axis[1], axis[2])
			val jm = m.toJoml()
			AxisAngle4f().set(m).approx(JAxisAngle4f().set(jm))
		}
	}

	context("JomlMath") {
		test("invsqrt matches") {
			for (v in listOf(0.5f, 1f, 2f, 4f, 100f)) {
				JomlMath.invsqrt(v).approx(org.joml.Math.invsqrt(v))
			}
		}

		test("safeAcos matches, including the out-of-range clamps") {
			for (v in listOf(-2f, -1f, -0.5f, 0f, 0.5f, 1f, 2f)) {
				JomlMath.safeAcos(v).approx(org.joml.Math.safeAcos(v))
			}
		}

		test("cosFromSin matches") {
			for (angle in listOf(-1.5f, 0f, 0.3f, 1.2f, 2.5f)) {
				val sin = kotlin.math.sin(angle)
				JomlMath.cosFromSin(sin, angle).approx(org.joml.Math.cosFromSin(sin, angle))
			}
		}
	}
})
