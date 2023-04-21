package helpers.displays.maths

import org.joml.Matrix4f
import java.nio.FloatBuffer

class Matrix(collection: Collection<Float> = listOf()) {
	constructor(array: FloatArray) : this(array.toMutableList())
	constructor(matrix: Matrix) : this(matrix.values.toMutableList())
	constructor(matrix: Matrix4f) : this() {
		this.matrix = matrix
	}

	constructor(buffer: FloatBuffer) : this() {
		matrix = Matrix4f(buffer)
	}

	var matrix = Matrix4f()

	val values get() = matrix.transpose().get(FloatArray(16)).toMutableList()
	val valuesAsArray: FloatArray get() = matrix.transpose().get(FloatArray(16))

	val isIdentity get() = matrix.properties() and Matrix4f.PROPERTY_IDENTITY.toInt() != 0
	val isTranslation get() = matrix.properties() and Matrix4f.PROPERTY_TRANSLATION.toInt() != 0
	val isScale get() = matrix.properties() and Matrix4f.PROPERTY_AFFINE.toInt() != 0
	val isRotation get() = matrix.properties() and Matrix4f.PROPERTY_ORTHONORMAL.toInt() != 0

	init {
		when {
			collection.isNotEmpty() -> set(*collection.toFloatArray())
			else -> setIdentity()
		}
	}

	operator fun get(row: Int, col: Int) = matrix[row, col]
	operator fun set(row: Int, col: Int, value: Float) {
		matrix[row, col] = value
	}

	operator fun times(other: Matrix) = apply { matrix.mul(other.matrix) }

	fun getTranslation() = Vec3f(matrix.m30(), matrix.m31(), matrix.m32())

	fun setIdentity() = apply { matrix.identity() }

	@Throws(IllegalArgumentException::class)
	fun set(vararg values: Float) {
		require(values.size == 16) { "Matrix must have exactly 16 elements" }
		matrix.setTransposed(values)
	}

	fun affine() = apply { matrix.scale(1 / matrix[3, 3]) }
	fun scale(vec: Vec3f) = apply { matrix.scale(vec.x, vec.y, vec.z) }
	fun translate(vec: Vec3f) = apply { matrix.translate(vec.x, vec.y, vec.z) }
	fun transpose() = apply { matrix.transpose() }
	fun rotate(quaternion: Quaternion) = apply { matrix.rotate(quaternion.w, quaternion.x, quaternion.y, quaternion.z) }
	fun rotate(axis: Vec3f, angle: Float) = apply { matrix.rotate(angle, axis.x, axis.y, axis.z) }

	fun add(other: Matrix) = apply { matrix.add(other.matrix) }
	fun invert() = apply { matrix.invert() }
	fun multiply(other: Matrix) = apply { matrix.mul(other.matrix) }
	fun subtract(other: Matrix) = apply { matrix.sub(other.matrix) }

	fun copy() = Matrix(this)

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as Matrix

		return matrix == other.matrix
	}

	override fun hashCode() = matrix.hashCode()

	override fun toString() = "Matrix(matrix=$matrix)"

	companion object {
		val IDENTITY = Matrix().apply(Matrix::setIdentity)

		fun fromRows(rows: List<List<Float>>) = Matrix(rows.flatMap {
			require(it.size == 4) { "Matrix row must have exactly 4 elements" }
			it.take(4)
		}.toMutableList())

		fun fromValues(vararg values: Float) = Matrix().apply {
			set(*values)
		}

		fun fromQuaternion(quaternion: Quaternion) = Matrix().apply {
			matrix.set(quaternion.quaternion)
		}
	}
}
