package io.github.ayfri.kore.helpers.displays.maths.internal

/** Minimal port of `org.joml.Matrix3f`, only the members used by the display transformation math. */
data class Matrix3f(
	var m00: Float = 1f, var m01: Float = 0f, var m02: Float = 0f,
	var m10: Float = 0f, var m11: Float = 1f, var m12: Float = 0f,
	var m20: Float = 0f, var m21: Float = 0f, var m22: Float = 1f,
) {
	constructor(other: Matrix3f) : this(
		other.m00, other.m01, other.m02,
		other.m10, other.m11, other.m12,
		other.m20, other.m21, other.m22,
	)

	/** Copies the upper-left 3x3 of [mat], matching JOML's `Matrix3f(Matrix4fc)`. */
	constructor(mat: Matrix4f) : this(
		mat.m00, mat.m01, mat.m02,
		mat.m10, mat.m11, mat.m12,
		mat.m20, mat.m21, mat.m22,
	)

	fun set(other: Matrix3f) = apply {
		m00 = other.m00; m01 = other.m01; m02 = other.m02
		m10 = other.m10; m11 = other.m11; m12 = other.m12
		m20 = other.m20; m21 = other.m21; m22 = other.m22
	}

	fun transpose() = apply {
		val nm01 = m10
		val nm02 = m20
		val nm10 = m01
		val nm12 = m21
		val nm20 = m02
		val nm21 = m12
		m01 = nm01; m02 = nm02
		m10 = nm10; m12 = nm12
		m20 = nm20; m21 = nm21
	}

	fun mul(right: Matrix3f) = apply {
		val nm00 = JomlMath.fma(m00, right.m00, JomlMath.fma(m10, right.m01, m20 * right.m02))
		val nm01 = JomlMath.fma(m01, right.m00, JomlMath.fma(m11, right.m01, m21 * right.m02))
		val nm02 = JomlMath.fma(m02, right.m00, JomlMath.fma(m12, right.m01, m22 * right.m02))
		val nm10 = JomlMath.fma(m00, right.m10, JomlMath.fma(m10, right.m11, m20 * right.m12))
		val nm11 = JomlMath.fma(m01, right.m10, JomlMath.fma(m11, right.m11, m21 * right.m12))
		val nm12 = JomlMath.fma(m02, right.m10, JomlMath.fma(m12, right.m11, m22 * right.m12))
		val nm20 = JomlMath.fma(m00, right.m20, JomlMath.fma(m10, right.m21, m20 * right.m22))
		val nm21 = JomlMath.fma(m01, right.m20, JomlMath.fma(m11, right.m21, m21 * right.m22))
		val nm22 = JomlMath.fma(m02, right.m20, JomlMath.fma(m12, right.m21, m22 * right.m22))
		m00 = nm00; m01 = nm01; m02 = nm02
		m10 = nm10; m11 = nm11; m12 = nm12
		m20 = nm20; m21 = nm21; m22 = nm22
	}

	/** Pre-multiplies this matrix by the rotation matrix derived from [quat] (`this = quat's rotation * this`). */
	fun rotate(quat: Quaternionf) = apply {
		val w2 = quat.w * quat.w
		val x2 = quat.x * quat.x
		val y2 = quat.y * quat.y
		val z2 = quat.z * quat.z
		val zw = quat.z * quat.w
		val dzw = zw + zw
		val xy = quat.x * quat.y
		val dxy = xy + xy
		val xz = quat.x * quat.z
		val dxz = xz + xz
		val yw = quat.y * quat.w
		val dyw = yw + yw
		val yz = quat.y * quat.z
		val dyz = yz + yz
		val xw = quat.x * quat.w
		val dxw = xw + xw
		val rm00 = w2 + x2 - z2 - y2
		val rm01 = dxy + dzw
		val rm02 = dxz - dyw
		val rm10 = dxy - dzw
		val rm11 = y2 - z2 + w2 - x2
		val rm12 = dyz + dxw
		val rm20 = dyw + dxz
		val rm21 = dyz - dxw
		val rm22 = z2 - y2 - x2 + w2
		val nm00 = m00 * rm00 + m10 * rm01 + m20 * rm02
		val nm01 = m01 * rm00 + m11 * rm01 + m21 * rm02
		val nm02 = m02 * rm00 + m12 * rm01 + m22 * rm02
		val nm10 = m00 * rm10 + m10 * rm11 + m20 * rm12
		val nm11 = m01 * rm10 + m11 * rm11 + m21 * rm12
		val nm12 = m02 * rm10 + m12 * rm11 + m22 * rm12
		val nm20 = m00 * rm20 + m10 * rm21 + m20 * rm22
		val nm21 = m01 * rm20 + m11 * rm21 + m21 * rm22
		val nm22 = m02 * rm20 + m12 * rm21 + m22 * rm22
		m00 = nm00; m01 = nm01; m02 = nm02
		m10 = nm10; m11 = nm11; m12 = nm12
		m20 = nm20; m21 = nm21; m22 = nm22
	}
}
