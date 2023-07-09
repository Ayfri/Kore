package helpers.displays.maths

import helpers.displays.MatrixBuilder
import helpers.displays.RotationBuilder
import helpers.displays.entities.DisplayEntity
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeCollection
import org.joml.Matrix3f

@Serializable(with = Transformation.Companion.TransformationSerializer::class)
data class Transformation(
	var matrix: Matrix? = null,
) {
	constructor(
		translation: Vec3f? = null,
		leftRotation: Quaternion = Quaternion.IDENTITY,
		scale: Vec3f = Vec3f(1f, 1f, 1f),
		rightRotation: Quaternion = Quaternion.IDENTITY,
	) : this() {
		this.translation = translation
		this.leftRotation = leftRotation
		this.scale = scale
		this.rightRotation = rightRotation
	}

	var translation: Vec3f? = null
	var leftRotation = Quaternion.IDENTITY
	var scale = Vec3f(1f, 1f, 1f)
	var rightRotation = Quaternion.IDENTITY

	val decomposed get() = matrix == null

	fun compose() {
		if (!decomposed) return
		matrix = Matrix().apply {
			translation?.let(::translate)
			multiply(Matrix.fromQuaternion(leftRotation))
			scale(scale)
			multiply(Matrix.fromQuaternion(rightRotation))
		}
	}

	fun interpolate(other: Transformation, t: Float) = Transformation().apply {
		translation = translation?.let { other.translation?.lerp(it, t) }
		leftRotation = leftRotation.slerp(other.leftRotation, t)
		scale = scale.lerp(other.scale, t)
		rightRotation = rightRotation.slerp(other.rightRotation, t)
	}

	fun invert() = Transformation().apply {
		translation = translation?.negate()
		leftRotation = leftRotation.conjugate
		scale = scale.reciprocal()
		rightRotation = rightRotation.conjugate
	}

	override fun toString() = when {
		decomposed -> "Transformation(translation=$translation, leftRotation=$leftRotation, scale=$scale, rightRotation=$rightRotation)"
		else -> "Transformation(matrix=$matrix)"
	}

	companion object {
		val IDENTITY = Transformation()

		object TransformationSerializer : KSerializer<Transformation> {
			override val descriptor = serialDescriptor<FloatArray>()

			override fun serialize(encoder: Encoder, value: Transformation) {
				var matrix = when {
					value.matrix != null -> value.matrix!!
					value.decomposed -> {
						value.compose()
						value.matrix!!
					}

					else -> Matrix.IDENTITY
				}

				val affine = matrix.copy().affine()
				val newTranslation = affine.getTranslation()
				val (newLeft, newScale, newRight) = Matrix3f(affine.matrix).svdDecompose()
				value.translation = newTranslation
				value.leftRotation = Quaternion(newLeft)
				value.scale = Vec3f(newScale)
				value.rightRotation = Quaternion(newRight)
				value.compose()

				matrix = value.matrix!!

				encoder.encodeCollection(serialDescriptor<FloatArray>(), matrix.values) { index, it ->
					encodeFloatElement(descriptor, index, it)
				}
			}

			@Throws(UnsupportedOperationException::class)
			override fun deserialize(decoder: Decoder): Transformation {
				throw UnsupportedOperationException("Deserialization not supported")
			}
		}
	}
}

fun DisplayEntity.transformation(block: Transformation.() -> Unit) {
	transformation = Transformation().apply(block)
}

fun Transformation.matrix(block: MatrixBuilder.() -> Unit) {
	matrix = MatrixBuilder().apply(block).build()
}

fun Transformation.leftRotation(block: RotationBuilder.() -> Unit) {
	leftRotation = RotationBuilder().apply(block).rotation
}

fun Transformation.rightRotation(block: RotationBuilder.() -> Unit) {
	rightRotation = RotationBuilder().apply(block).rotation
}

fun Transformation.scale(block: Vec3f.() -> Unit) {
	scale = Vec3f().apply(block)
}

fun Transformation.translation(block: Vec3f.() -> Unit) {
	translation = Vec3f().apply(block)
}
