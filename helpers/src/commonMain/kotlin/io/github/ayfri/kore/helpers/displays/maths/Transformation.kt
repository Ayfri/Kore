package io.github.ayfri.kore.helpers.displays.maths

import io.github.ayfri.kore.arguments.maths.Vec3f
import io.github.ayfri.kore.helpers.displays.MatrixBuilder
import io.github.ayfri.kore.helpers.displays.RotationBuilder
import io.github.ayfri.kore.helpers.displays.entities.DisplayEntity
import io.github.ayfri.kore.helpers.displays.maths.internal.Matrix3f
import io.github.ayfri.kore.helpers.maths.Vec3f
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.benwoodworth.knbt.NbtTag

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
		if (decomposed) matrix = toMatrix()
	}

	/** Returns this transformation split into translation, rotations and scale, read back from [matrix] with an SVD when it is set. */
	fun decompose(): Transformation {
		val affine = matrix?.copy()?.affine() ?: return this
		val (left, scale, right) = Matrix3f(affine.matrix).svdDecompose()
		return Transformation(affine.getTranslation(), Quaternion(left), Vec3f(scale), Quaternion(right))
	}

	/** Returns the transformation [t] of the way from this one to [other], `0` being this one and `1` being [other]. */
	fun interpolate(other: Transformation, t: Float): Transformation {
		val from = decompose()
		val to = other.decompose()
		val translation = when {
			from.translation == null && to.translation == null -> null
			else -> (from.translation ?: Vec3f()).lerp(to.translation ?: Vec3f(), t)
		}
		return Transformation(
			translation,
			Quaternion.slerp(from.leftRotation, to.leftRotation, t),
			from.scale.lerp(to.scale, t),
			Quaternion.slerp(from.rightRotation, to.rightRotation, t),
		)
	}

	/** Returns the transformation undoing this one, as a matrix. */
	fun invert() = Transformation(toMatrix().invert())

	private fun toMatrix() = matrix?.copy() ?: Matrix().apply {
		translation?.let(::translate)
		multiply(Matrix.fromQuaternion(leftRotation))
		scale(scale)
		multiply(Matrix.fromQuaternion(rightRotation))
	}

	override fun toString() = when {
		decomposed -> "Transformation(translation=$translation, leftRotation=$leftRotation, scale=$scale, rightRotation=$rightRotation)"
		else -> "Transformation(matrix=$matrix)"
	}

	companion object {
		/** A new identity transformation on each access, so mutating one can't leak into the others. */
		val IDENTITY get() = Transformation()

		@Serializable
		private data class TransformationParts(
			val leftRotation: List<Float>,
			val rightRotation: List<Float>,
			val scale: List<Float>,
			val translation: List<Float>,
		)

		object TransformationSerializer : KSerializer<Transformation> {
			override val descriptor = SerialDescriptor("Transformation", NbtTag.serializer().descriptor)

			override fun serialize(encoder: Encoder, value: Transformation) {
				val matrix = value.matrix
				if (matrix != null) return encoder.encodeSerializableValue(ListSerializer(Float.serializer()), matrix.values)

				val translation = value.translation ?: Vec3f()
				encoder.encodeSerializableValue(
					TransformationParts.serializer(),
					TransformationParts(
						leftRotation = value.leftRotation.run { listOf(x, y, z, w) },
						rightRotation = value.rightRotation.run { listOf(x, y, z, w) },
						scale = value.scale.run { listOf(x, y, z) },
						translation = translation.run { listOf(x, y, z) },
					)
				)
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

/** Edits a copy of the current scale, `(1, 1, 1)` by default: `scale { x = 2f }` gives `(2, 1, 1)`. */
fun Transformation.scale(block: Vec3f.() -> Unit) {
	scale = scale.copy().apply(block)
}

/** Edits a copy of the current translation, `(0, 0, 0)` by default: `translation { y = 2f }` gives `(0, 2, 0)`. */
fun Transformation.translation(block: Vec3f.() -> Unit) {
	translation = (translation ?: Vec3f()).copy().apply(block)
}
