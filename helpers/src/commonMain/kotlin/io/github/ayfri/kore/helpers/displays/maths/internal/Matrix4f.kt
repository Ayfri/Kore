package io.github.ayfri.kore.helpers.displays.maths.internal

/**
 * Minimal port of `org.joml.Matrix4f`, only the members used by the display transformation math.
 * Field naming follows JOML's convention: `mCR` where `C` is the column and `R` is the row.
 */
class Matrix4f {
	var m00 = 1f
	var m01 = 0f
	var m02 = 0f
	var m03 = 0f
	var m10 = 0f
	var m11 = 1f
	var m12 = 0f
	var m13 = 0f
	var m20 = 0f
	var m21 = 0f
	var m22 = 1f
	var m23 = 0f
	var m30 = 0f
	var m31 = 0f
	var m32 = 0f
	var m33 = 1f

	fun identity() = apply {
		m00 = 1f; m01 = 0f; m02 = 0f; m03 = 0f
		m10 = 0f; m11 = 1f; m12 = 0f; m13 = 0f
		m20 = 0f; m21 = 0f; m22 = 1f; m23 = 0f
		m30 = 0f; m31 = 0f; m32 = 0f; m33 = 1f
	}

	operator fun get(column: Int, row: Int): Float = when (column) {
		0 -> when (row) {
			0 -> m00; 1 -> m01; 2 -> m02; else -> m03
		}

		1 -> when (row) {
			0 -> m10; 1 -> m11; 2 -> m12; else -> m13
		}

		2 -> when (row) {
			0 -> m20; 1 -> m21; 2 -> m22; else -> m23
		}

		else -> when (row) {
			0 -> m30; 1 -> m31; 2 -> m32; else -> m33
		}
	}

	operator fun set(column: Int, row: Int, value: Float) {
		when (column) {
			0 -> when (row) {
				0 -> m00 = value; 1 -> m01 = value; 2 -> m02 = value; else -> m03 = value
			}

			1 -> when (row) {
				0 -> m10 = value; 1 -> m11 = value; 2 -> m12 = value; else -> m13 = value
			}

			2 -> when (row) {
				0 -> m20 = value; 1 -> m21 = value; 2 -> m22 = value; else -> m23 = value
			}

			else -> when (row) {
				0 -> m30 = value; 1 -> m31 = value; 2 -> m32 = value; else -> m33 = value
			}
		}
	}

	/** Column-major dump of this matrix's 16 elements, matching JOML's `Matrix4f.get(float[])`. */
	fun get(dest: FloatArray): FloatArray {
		dest[0] = m00; dest[1] = m01; dest[2] = m02; dest[3] = m03
		dest[4] = m10; dest[5] = m11; dest[6] = m12; dest[7] = m13
		dest[8] = m20; dest[9] = m21; dest[10] = m22; dest[11] = m23
		dest[12] = m30; dest[13] = m31; dest[14] = m32; dest[15] = m33
		return dest
	}

	/** Sets this matrix's elements by reading [values] as a row-major 4x4 array, matching JOML's `setTransposed(float[])`. */
	fun setTransposed(values: FloatArray) = apply {
		m00 = values[0]; m10 = values[1]; m20 = values[2]; m30 = values[3]
		m01 = values[4]; m11 = values[5]; m21 = values[6]; m31 = values[7]
		m02 = values[8]; m12 = values[9]; m22 = values[10]; m32 = values[11]
		m03 = values[12]; m13 = values[13]; m23 = values[14]; m33 = values[15]
	}

	fun set(other: Matrix4f) = apply {
		m00 = other.m00; m01 = other.m01; m02 = other.m02; m03 = other.m03
		m10 = other.m10; m11 = other.m11; m12 = other.m12; m13 = other.m13
		m20 = other.m20; m21 = other.m21; m22 = other.m22; m23 = other.m23
		m30 = other.m30; m31 = other.m31; m32 = other.m32; m33 = other.m33
	}

	/** Sets this to the rotation matrix equivalent to [quat], matching JOML's `Matrix4f.set(Quaternionfc)`. */
	fun set(quat: Quaternionf) = apply {
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
		identity()
		m00 = w2 + x2 - z2 - y2
		m01 = dxy + dzw
		m02 = dxz - dyw
		m10 = -dzw + dxy
		m11 = y2 - z2 + w2 - x2
		m12 = dyz + dxw
		m20 = dyw + dxz
		m21 = dyz - dxw
		m22 = z2 - y2 - x2 + w2
	}

	fun transpose() = apply {
		val n01 = m10
		val n02 = m20
		val n03 = m30
		val n10 = m01
		val n12 = m21
		val n13 = m31
		val n20 = m02
		val n21 = m12
		val n23 = m32
		val n30 = m03
		val n31 = m13
		val n32 = m23
		m01 = n01; m02 = n02; m03 = n03
		m10 = n10; m12 = n12; m13 = n13
		m20 = n20; m21 = n21; m23 = n23
		m30 = n30; m31 = n31; m32 = n32
	}

	fun add(other: Matrix4f) = apply {
		m00 += other.m00; m01 += other.m01; m02 += other.m02; m03 += other.m03
		m10 += other.m10; m11 += other.m11; m12 += other.m12; m13 += other.m13
		m20 += other.m20; m21 += other.m21; m22 += other.m22; m23 += other.m23
		m30 += other.m30; m31 += other.m31; m32 += other.m32; m33 += other.m33
	}

	fun sub(other: Matrix4f) = apply {
		m00 -= other.m00; m01 -= other.m01; m02 -= other.m02; m03 -= other.m03
		m10 -= other.m10; m11 -= other.m11; m12 -= other.m12; m13 -= other.m13
		m20 -= other.m20; m21 -= other.m21; m22 -= other.m22; m23 -= other.m23
		m30 -= other.m30; m31 -= other.m31; m32 -= other.m32; m33 -= other.m33
	}

	fun mul(right: Matrix4f) = apply {
		val nm00 =
			JomlMath.fma(m00, right.m00, JomlMath.fma(m10, right.m01, JomlMath.fma(m20, right.m02, m30 * right.m03)))
		val nm01 =
			JomlMath.fma(m01, right.m00, JomlMath.fma(m11, right.m01, JomlMath.fma(m21, right.m02, m31 * right.m03)))
		val nm02 =
			JomlMath.fma(m02, right.m00, JomlMath.fma(m12, right.m01, JomlMath.fma(m22, right.m02, m32 * right.m03)))
		val nm03 =
			JomlMath.fma(m03, right.m00, JomlMath.fma(m13, right.m01, JomlMath.fma(m23, right.m02, m33 * right.m03)))
		val nm10 =
			JomlMath.fma(m00, right.m10, JomlMath.fma(m10, right.m11, JomlMath.fma(m20, right.m12, m30 * right.m13)))
		val nm11 =
			JomlMath.fma(m01, right.m10, JomlMath.fma(m11, right.m11, JomlMath.fma(m21, right.m12, m31 * right.m13)))
		val nm12 =
			JomlMath.fma(m02, right.m10, JomlMath.fma(m12, right.m11, JomlMath.fma(m22, right.m12, m32 * right.m13)))
		val nm13 =
			JomlMath.fma(m03, right.m10, JomlMath.fma(m13, right.m11, JomlMath.fma(m23, right.m12, m33 * right.m13)))
		val nm20 =
			JomlMath.fma(m00, right.m20, JomlMath.fma(m10, right.m21, JomlMath.fma(m20, right.m22, m30 * right.m23)))
		val nm21 =
			JomlMath.fma(m01, right.m20, JomlMath.fma(m11, right.m21, JomlMath.fma(m21, right.m22, m31 * right.m23)))
		val nm22 =
			JomlMath.fma(m02, right.m20, JomlMath.fma(m12, right.m21, JomlMath.fma(m22, right.m22, m32 * right.m23)))
		val nm23 =
			JomlMath.fma(m03, right.m20, JomlMath.fma(m13, right.m21, JomlMath.fma(m23, right.m22, m33 * right.m23)))
		val nm30 =
			JomlMath.fma(m00, right.m30, JomlMath.fma(m10, right.m31, JomlMath.fma(m20, right.m32, m30 * right.m33)))
		val nm31 =
			JomlMath.fma(m01, right.m30, JomlMath.fma(m11, right.m31, JomlMath.fma(m21, right.m32, m31 * right.m33)))
		val nm32 =
			JomlMath.fma(m02, right.m30, JomlMath.fma(m12, right.m31, JomlMath.fma(m22, right.m32, m32 * right.m33)))
		val nm33 =
			JomlMath.fma(m03, right.m30, JomlMath.fma(m13, right.m31, JomlMath.fma(m23, right.m32, m33 * right.m33)))
		m00 = nm00; m01 = nm01; m02 = nm02; m03 = nm03
		m10 = nm10; m11 = nm11; m12 = nm12; m13 = nm13
		m20 = nm20; m21 = nm21; m22 = nm22; m23 = nm23
		m30 = nm30; m31 = nm31; m32 = nm32; m33 = nm33
	}

	fun scale(xyz: Float) = scale(xyz, xyz, xyz)

	fun scale(x: Float, y: Float, z: Float) = apply {
		m00 *= x; m01 *= x; m02 *= x; m03 *= x
		m10 *= y; m11 *= y; m12 *= y; m13 *= y
		m20 *= z; m21 *= z; m22 *= z; m23 *= z
	}

	fun translate(x: Float, y: Float, z: Float) = apply {
		m30 = JomlMath.fma(m00, x, JomlMath.fma(m10, y, JomlMath.fma(m20, z, m30)))
		m31 = JomlMath.fma(m01, x, JomlMath.fma(m11, y, JomlMath.fma(m21, z, m31)))
		m32 = JomlMath.fma(m02, x, JomlMath.fma(m12, y, JomlMath.fma(m22, z, m32)))
		m33 = JomlMath.fma(m03, x, JomlMath.fma(m13, y, JomlMath.fma(m23, z, m33)))
	}

	/** Applies a rotation of [ang] radians about the unit axis ([x], [y], [z]), matching JOML's `Matrix4f.rotate(float,float,float,float)`. */
	fun rotate(ang: Float, x: Float, y: Float, z: Float) = apply {
		val s = JomlMath.sin(ang)
		val c = JomlMath.cosFromSin(s, ang)
		val invC = 1f - c
		val xx = x * x
		val xy = x * y
		val xz = x * z
		val yy = y * y
		val yz = y * z
		val zz = z * z
		val rm00 = xx * invC + c
		val rm01 = xy * invC + z * s
		val rm02 = xz * invC - y * s
		val rm10 = xy * invC - z * s
		val rm11 = yy * invC + c
		val rm12 = yz * invC + x * s
		val rm20 = xz * invC + y * s
		val rm21 = yz * invC - x * s
		val rm22 = zz * invC + c
		val nm00 = m00 * rm00 + m10 * rm01 + m20 * rm02
		val nm01 = m01 * rm00 + m11 * rm01 + m21 * rm02
		val nm02 = m02 * rm00 + m12 * rm01 + m22 * rm02
		val nm03 = m03 * rm00 + m13 * rm01 + m23 * rm02
		val nm10 = m00 * rm10 + m10 * rm11 + m20 * rm12
		val nm11 = m01 * rm10 + m11 * rm11 + m21 * rm12
		val nm12 = m02 * rm10 + m12 * rm11 + m22 * rm12
		val nm13 = m03 * rm10 + m13 * rm11 + m23 * rm12
		val nm20 = m00 * rm20 + m10 * rm21 + m20 * rm22
		val nm21 = m01 * rm20 + m11 * rm21 + m21 * rm22
		val nm22 = m02 * rm20 + m12 * rm21 + m22 * rm22
		val nm23 = m03 * rm20 + m13 * rm21 + m23 * rm22
		m00 = nm00; m01 = nm01; m02 = nm02; m03 = nm03
		m10 = nm10; m11 = nm11; m12 = nm12; m13 = nm13
		m20 = nm20; m21 = nm21; m22 = nm22; m23 = nm23
	}

	fun invert() = apply {
		val a = m00 * m11 - m01 * m10
		val b = m00 * m12 - m02 * m10
		val c = m00 * m13 - m03 * m10
		val d = m01 * m12 - m02 * m11
		val e = m01 * m13 - m03 * m11
		val f = m02 * m13 - m03 * m12
		val g = m20 * m31 - m21 * m30
		val h = m20 * m32 - m22 * m30
		val i = m20 * m33 - m23 * m30
		val j = m21 * m32 - m22 * m31
		val k = m21 * m33 - m23 * m31
		val l = m22 * m33 - m23 * m32
		val det = 1f / (a * l - b * k + c * j + d * i - e * h + f * g)

		val nm00 = JomlMath.fma(m11, l, JomlMath.fma(-m12, k, m13 * j)) * det
		val nm01 = JomlMath.fma(-m01, l, JomlMath.fma(m02, k, -m03 * j)) * det
		val nm02 = JomlMath.fma(m31, f, JomlMath.fma(-m32, e, m33 * d)) * det
		val nm03 = JomlMath.fma(-m21, f, JomlMath.fma(m22, e, -m23 * d)) * det
		val nm10 = JomlMath.fma(-m10, l, JomlMath.fma(m12, i, -m13 * h)) * det
		val nm11 = JomlMath.fma(m00, l, JomlMath.fma(-m02, i, m03 * h)) * det
		val nm12 = JomlMath.fma(-m30, f, JomlMath.fma(m32, c, -m33 * b)) * det
		val nm13 = JomlMath.fma(m20, f, JomlMath.fma(-m22, c, m23 * b)) * det
		val nm20 = JomlMath.fma(m10, k, JomlMath.fma(-m11, i, m13 * g)) * det
		val nm21 = JomlMath.fma(-m00, k, JomlMath.fma(m01, i, -m03 * g)) * det
		val nm22 = JomlMath.fma(m30, e, JomlMath.fma(-m31, c, m33 * a)) * det
		val nm23 = JomlMath.fma(-m20, e, JomlMath.fma(m21, c, -m23 * a)) * det
		val nm30 = JomlMath.fma(-m10, j, JomlMath.fma(m11, h, -m12 * g)) * det
		val nm31 = JomlMath.fma(m00, j, JomlMath.fma(-m01, h, m02 * g)) * det
		val nm32 = JomlMath.fma(-m30, d, JomlMath.fma(m31, b, -m32 * a)) * det
		val nm33 = JomlMath.fma(m20, d, JomlMath.fma(-m21, b, m22 * a)) * det

		m00 = nm00; m01 = nm01; m02 = nm02; m03 = nm03
		m10 = nm10; m11 = nm11; m12 = nm12; m13 = nm13
		m20 = nm20; m21 = nm21; m22 = nm22; m23 = nm23
		m30 = nm30; m31 = nm31; m32 = nm32; m33 = nm33
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Matrix4f) return false
		return m00 == other.m00 && m01 == other.m01 && m02 == other.m02 && m03 == other.m03 &&
			m10 == other.m10 && m11 == other.m11 && m12 == other.m12 && m13 == other.m13 &&
			m20 == other.m20 && m21 == other.m21 && m22 == other.m22 && m23 == other.m23 &&
			m30 == other.m30 && m31 == other.m31 && m32 == other.m32 && m33 == other.m33
	}

	override fun hashCode(): Int {
		var result = m00.hashCode()
		for (v in floatArrayOf(m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33)) {
			result = 31 * result + v.hashCode()
		}
		return result
	}

	override fun toString() =
		"Matrix4f($m00, $m01, $m02, $m03, $m10, $m11, $m12, $m13, $m20, $m21, $m22, $m23, $m30, $m31, $m32, $m33)"
}
