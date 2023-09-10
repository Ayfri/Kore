package io.github.ayfri.kore.helpers.displays.maths

import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.sqrt
import org.joml.Matrix3f
import org.joml.Quaternionf
import org.joml.Vector3f

val COTANGENT_PI_OVER_8 = 3.0f + 2.0f * sqrt(2.0f)
val SIN_COS_PI_OVER_8 = GivensPair.fromAngle(0.7853982f)

private fun approximateGivensQuaternion(diagonalElement1: Float, offDiagonalElement: Float, diagonalElement2: Float): GivensPair {
	val f = 2.0f * (diagonalElement1 - diagonalElement2)
	return when {
		COTANGENT_PI_OVER_8 * offDiagonalElement * offDiagonalElement < f * f -> GivensPair.normalize(offDiagonalElement, f)
		else -> SIN_COS_PI_OVER_8
	}
}

private fun applyJacobiIteration(
	currentMatrix: Matrix3f,
	tempMatrix: Matrix3f,
	currentQuaternion: Quaternionf,
	tempQuaternion: Quaternionf,
) {
	var newQuaternion: Quaternionf
	var givensPair: GivensPair
	if (currentMatrix.m01 * currentMatrix.m01 + currentMatrix.m10 * currentMatrix.m10 > 1.0E-6f) {
		givensPair = approximateGivensQuaternion(currentMatrix.m00, 0.5f * (currentMatrix.m01 + currentMatrix.m10), currentMatrix.m11)
		newQuaternion = givensPair.setQuaternionIMagY(currentQuaternion)
		tempQuaternion.mul(newQuaternion)
		givensPair.setMatrix3fForRotationAboutZ(tempMatrix)
		currentMatrix.updateWithTransposed(tempMatrix)
	}
	if (currentMatrix.m02 * currentMatrix.m02 + currentMatrix.m20 * currentMatrix.m20 > 1.0E-6f) {
		givensPair =
			approximateGivensQuaternion(currentMatrix.m00, 0.5f * (currentMatrix.m02 + currentMatrix.m20), currentMatrix.m22).negateSin()
		newQuaternion = givensPair.setQuaternionIMagX(currentQuaternion)
		tempQuaternion.mul(newQuaternion)
		givensPair.setMatrix3fForRotationAboutY(tempMatrix)
		currentMatrix.updateWithTransposed(tempMatrix)
	}
	if (currentMatrix.m12 * currentMatrix.m12 + currentMatrix.m21 * currentMatrix.m21 > 1.0E-6f) {
		givensPair = approximateGivensQuaternion(currentMatrix.m11, 0.5f * (currentMatrix.m12 + currentMatrix.m21), currentMatrix.m22)
		newQuaternion = givensPair.setQuaternionRealPart(currentQuaternion)
		tempQuaternion.mul(newQuaternion)
		givensPair.setMatrix3fForRotationAboutX(tempMatrix)
		currentMatrix.updateWithTransposed(tempMatrix)
	}
}

fun Matrix3f.getSVDQuaternion(iterations: Int): Quaternionf {
	val quaternion = Quaternionf()
	val transposedMatrix = Matrix3f()
	val quaternion2 = Quaternionf()
	repeat(iterations) {
		applyJacobiIteration(this, transposedMatrix, quaternion2, quaternion)
	}
	quaternion.normalize()
	return quaternion
}

private fun Matrix3f.updateWithTransposed(transpose: Matrix3f) {
	mul(transpose)
	transpose.transpose()
	transpose.mul(this)
	set(transpose)
}

private fun qrGivensQuaternion(x: Float, y: Float): GivensPair {
	val norm = hypot(x.toDouble(), y.toDouble()).toFloat()
	var sin = if (norm > 1.0E-6f) y else 0.0f
	var cos = abs(x) + max(norm, 1.0E-6f)
	if (x < 0.0f) sin = cos.also { cos = sin }

	return GivensPair.normalize(sin, cos)
}

fun Matrix3f.svdDecompose(): Triple<Quaternionf, Vector3f, Quaternionf> {
	val workingMatrix1 = Matrix3f(this)
	workingMatrix1.transpose()
	workingMatrix1.mul(this)

	val quaternion1 = workingMatrix1.getSVDQuaternion(5)
	val a = workingMatrix1.m00
	val b = workingMatrix1.m11
	val bl = a.toDouble() < 1.0E-6
	val bl2 = b.toDouble() < 1.0E-6
	var workingMatrix2 = workingMatrix1
	val workingMatrix3 = rotate(quaternion1)
	val quaternion2 = Quaternionf()
	val quaternion3 = Quaternionf()
	var givensPair: GivensPair = if (bl) qrGivensQuaternion(
		workingMatrix3.m11,
		-workingMatrix3.m10
	) else qrGivensQuaternion(workingMatrix3.m00, workingMatrix3.m01)

	val quaternion4 = givensPair.setQuaternionIMagY(quaternion3)
	val workingMatrix4 = givensPair.setMatrix3fForRotationAboutZ(workingMatrix2)
	quaternion2.mul(quaternion4)
	workingMatrix4.transpose().mul(workingMatrix3)
	workingMatrix2 = workingMatrix3

	givensPair = if (bl) qrGivensQuaternion(
		workingMatrix4.m22,
		-workingMatrix4.m20
	) else qrGivensQuaternion(workingMatrix4.m00, workingMatrix4.m02)

	givensPair = givensPair.negateSin()

	val quaternion5 = givensPair.setQuaternionIMagX(quaternion3)
	val workingMatrix5 = givensPair.setMatrix3fForRotationAboutY(workingMatrix2)
	quaternion2.mul(quaternion5)
	workingMatrix5.transpose().mul(workingMatrix4)
	workingMatrix2 = workingMatrix4
	givensPair = if (bl2) qrGivensQuaternion(
		workingMatrix5.m22,
		-workingMatrix5.m21
	) else qrGivensQuaternion(workingMatrix5.m11, workingMatrix5.m12)

	val quaternion6 = givensPair.setQuaternionRealPart(quaternion3)
	val workingMatrix6 = givensPair.setMatrix3fForRotationAboutX(workingMatrix2)
	quaternion2.mul(quaternion6)
	workingMatrix6.transpose().mul(workingMatrix5)

	val vector = Vector3f(workingMatrix6.m00, workingMatrix6.m11, workingMatrix6.m22)
	return Triple(quaternion2, vector, quaternion1.conjugate())
}
