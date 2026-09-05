package io.github.ayfri.kore.helpers.displays.maths

import io.github.ayfri.kore.helpers.displays.maths.internal.Matrix3f
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.shouldBe
import kotlin.math.PI

class TransformationRoundTripTests : FunSpec({
	val eps = 1e-3f

	fun Float.approx(expected: Float) = this shouldBe (expected plusOrMinus eps)
	fun List<Float>.approx(expected: List<Float>) {
		size shouldBe expected.size
		indices.forEach { i -> this[i].approx(expected[i]) }
	}

	fun decompose(matrix: Matrix): Transformation {
		val affine = matrix.copy().affine()
		val translation = affine.getTranslation()
		val (left, scale, right) = Matrix3f(affine.matrix).svdDecompose()
		return Transformation(
			translation = translation,
			leftRotation = Quaternion(left),
			scale = Vec3f(scale),
			rightRotation = Quaternion(right),
		)
	}

	fun assertRoundTrips(t: Transformation) {
		t.compose()
		val originalValues = t.matrix!!.values.toList()

		val recomposed = decompose(Matrix.fromValues(*originalValues.toFloatArray()))
		recomposed.compose()
		recomposed.matrix!!.values.toList().approx(originalValues)
	}

	test("identity transformation composes to the identity matrix") {
		val t = Transformation()
		t.compose()
		t.matrix!!.values.toList().approx(Matrix.IDENTITY.values.toList())
	}

	test("pure translation composes with the expected translation column and round-trips") {
		val t = Transformation(translation = Vec3f(1f, 2f, 3f))
		t.compose()
		val translation = t.matrix!!.getTranslation()
		translation.x.approx(1f); translation.y.approx(2f); translation.z.approx(3f)

		assertRoundTrips(Transformation(translation = Vec3f(1f, 2f, 3f)))
	}

	test("pure uniform scale round-trips") {
		assertRoundTrips(Transformation(scale = Vec3f(2f, 2f, 2f)))
	}

	test("pure non-uniform scale round-trips") {
		assertRoundTrips(Transformation(scale = Vec3f(0.5f, 1.5f, 3f)))
	}

	test("a 90-degree left rotation about Y maps +Z to +X as expected") {
		val q = Quaternion.fromEulerAngles(0f, (PI / 2).toFloat(), 0f)
		val t = Transformation(leftRotation = q)
		t.compose()

		val m = t.matrix!!
		m[2, 0].approx(1f)
		m[2, 1].approx(0f)
		m[2, 2].approx(0f)
	}

	test("a left rotation round-trips") {
		val q = Quaternion.fromEulerAngles(0f, (PI / 2).toFloat(), 0f)
		assertRoundTrips(Transformation(leftRotation = q))
	}

	test("a combined translation, rotation, and uniform scale round-trips") {
		val q = Quaternion.fromEulerAngles(0.4f, 0.6f, 0.9f)
		assertRoundTrips(
			Transformation(
				translation = Vec3f(1.5f, -2.5f, 3.5f),
				leftRotation = q,
				scale = Vec3f(1.2f, 1.2f, 1.2f),
			)
		)
	}

	test("a combined translation, rotation, and non-uniform scale round-trips") {
		val q = Quaternion.fromEulerAngles(0.1f, 0.9f, 1.4f)
		assertRoundTrips(
			Transformation(
				translation = Vec3f(-1f, 4f, 0.25f),
				leftRotation = q,
				scale = Vec3f(0.75f, 1.25f, 2f),
			)
		)
	}

	test("fromAxisAngle produces a real unit rotation quaternion") {
		val axis = Vec3f(0f, 1f, 0f)
		val angle = (PI / 2).toFloat()
		val q = Quaternion.fromAxisAngle(axis, angle)

		q.x.approx(0f)
		q.y.approx(0.70710677f)
		q.z.approx(0f)
		q.w.approx(0.70710677f)
		q.lengthSquared.approx(1f)
	}

	test("a 90-degree left rotation built from fromAxisAngle maps +Z to +X as expected") {
		val q = Quaternion.fromAxisAngle(Vec3f.Y_AXIS, (PI / 2).toFloat())
		val t = Transformation(leftRotation = q)
		t.compose()

		val m = t.matrix!!
		m[2, 0].approx(1f)
		m[2, 1].approx(0f)
		m[2, 2].approx(0f)
	}

	test("a rotation built from fromAxisAngle round-trips") {
		val q = Quaternion.fromAxisAngle(Vec3f(0.4f, 0.6f, 0.6928203f), 0.9f)
		assertRoundTrips(Transformation(leftRotation = q))
	}
})
