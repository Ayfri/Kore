package helpers.displays

import helpers.displays.maths.*

class MatrixBuilder {
	private val elements = mutableListOf<Float>()

	fun row(vararg values: Float) {
		elements.addAll(values.take(4).toTypedArray())
	}

	fun build(): Matrix {
		require(elements.size == 16) { "Matrix must have exactly 16 elements" }
		return Matrix(elements)
	}
}

class AxisAngleBuilder {
	private var angle = 0f
	private var axis = Vec3f()

	fun angle(value: Float) {
		angle = value
	}

	fun axis(value: Vec3f) {
		axis = value
	}

	fun build() = AxisAngle(angle, axis.x, axis.y, axis.z)
}

class RotationBuilder(private val transform: Transformation) {
	var rotation = Quaternion()

	fun quaternion(block: Quaternion.() -> Unit) {
		rotation = Quaternion.IDENTITY.copy().apply(block).normalize()
	}

	fun quaternion(x: Number, y: Number, z: Number, w: Number) {
		rotation = Quaternion(x, y, z, w).normalize()
	}

	fun quaternionNormalized(block: Quaternion.() -> Unit) {
		rotation = Quaternion.IDENTITY.copy().apply(block)
	}

	fun quaternionNormalized(x: Number, y: Number, z: Number, w: Number) {
		rotation = Quaternion(x, y, z, w)
	}

	fun axisAngle(block: AxisAngleBuilder.() -> Unit) {
		rotation = AxisAngleBuilder().apply(block).build().toQuaternion().normalize()
	}

	fun axisAngle(axis: Vec3f, angle: Float) {
		rotation = Quaternion.fromAxisAngle(axis, angle).normalize()
	}

	fun axisAngleNormalized(block: AxisAngleBuilder.() -> Unit) {
		rotation = AxisAngleBuilder().apply(block).build().toQuaternion()
	}

	fun axisAngleNormalized(axis: Vec3f, angle: Float) {
		rotation = Quaternion.fromAxisAngle(axis, angle)
	}

	fun eulerAngles(block: Vec3f.() -> Unit) {
		rotation = Vec3f().apply(block).toQuaternion().normalize()
	}

	fun eulerAngles(x: Float, y: Float, z: Float) {
		rotation = Quaternion.fromEulerAngles(x, y, z).normalize()
	}

	fun eulerAnglesNormalized(block: Vec3f.() -> Unit) {
		rotation = Vec3f().apply(block).toQuaternion()
	}

	fun eulerAnglesNormalized(x: Float, y: Float, z: Float) {
		rotation = Quaternion.fromEulerAngles(x, y, z)
	}
}
