package io.github.ayfri.kore.arguments.maths

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.FloatArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.math.absoluteValue
import kotlin.math.sqrt

/** [Vec3f] serialized as a `[x, y, z]` float array, the shape display transformations and enchantment effects use. */
typealias Vec3fAsArray = @Serializable(Vec3f.Companion.Vec3fAsArraySerializer::class) Vec3f

/**
 * A float vector for data-driven JSON (display transformations, enchantment impulses, ...), unlike [Vec3] which is a
 * command coordinate carrying `~` / `^` types.
 *
 * ```kotlin
 * Vec3f(1f, 2f, 2f).normalize() // (0.333, 0.667, 0.667)
 * Vec3f.UP * 0.5f // (0, 0.5, 0)
 * ```
 */
@Serializable
data class Vec3f(var x: Float = 0f, var y: Float = 0f, var z: Float = 0f) {
	constructor(x: Number, y: Number, z: Number) : this(x.toFloat(), y.toFloat(), z.toFloat())
	constructor(vec3: Vec3) : this(vec3.x.value, vec3.y.value, vec3.z.value)

	val length get() = sqrt(x * x + y * y + z * z)
	val lengthSquared get() = x * x + y * y + z * z

	operator fun unaryMinus() = Vec3f(-x, -y, -z)
	operator fun plus(other: Vec3f) = Vec3f(x + other.x, y + other.y, z + other.z)
	operator fun minus(other: Vec3f) = Vec3f(x - other.x, y - other.y, z - other.z)
	operator fun times(scalar: Float) = Vec3f(x * scalar, y * scalar, z * scalar)
	operator fun div(scalar: Float) = Vec3f(x / scalar, y / scalar, z / scalar)

	fun abs() = Vec3f(x.absoluteValue, y.absoluteValue, z.absoluteValue)

	fun cross(other: Vec3f) = Vec3f(
		y * other.z - z * other.y,
		z * other.x - x * other.z,
		x * other.y - y * other.x
	)

	fun distance(other: Vec3f) = (this - other).length
	fun distanceSquared(other: Vec3f) = (this - other).lengthSquared

	fun dot(other: Vec3f) = x * other.x + y * other.y + z * other.z
	/** Linear interpolation, `t = 0` gives this vector and `t = 1` gives [other]. */
	fun lerp(other: Vec3f, t: Float) = this + (other - this) * t

	fun negate() = -this

	/** This vector scaled to a length of `1`, a zero vector stays zero. */
	fun normalize() = when (val length = length) {
		0f -> ZERO
		else -> this / length
	}

	/** Component-wise `1 / value`, a zero component becomes infinity. */
	fun reciprocal() = Vec3f(1f / x, 1f / y, 1f / z)

	fun toArray() = floatArrayOf(x, y, z)

	companion object {
		val ZERO = Vec3f()
		val X_AXIS = Vec3f(1f)
		val Y_AXIS = Vec3f(y = 1f)
		val Z_AXIS = Vec3f(z = 1f)

		/** Directions of Minecraft's local frame (`^x ^y ^z`): +X is left and +Z is forward for an entity facing south. */
		val LEFT = X_AXIS
		val RIGHT = -X_AXIS
		val UP = Y_AXIS
		val DOWN = -Y_AXIS
		val FORWARD = Z_AXIS
		val BACKWARD = -Z_AXIS

		fun fromArray(array: FloatArray) = Vec3f(array[0], array[1], array[2])

		data object Vec3fAsArraySerializer : KSerializer<Vec3f> {
			private val delegateSerializer = FloatArraySerializer()
			override val descriptor: SerialDescriptor = delegateSerializer.descriptor

			override fun deserialize(decoder: Decoder) = fromArray(decoder.decodeSerializableValue(delegateSerializer))

			override fun serialize(encoder: Encoder, value: Vec3f) =
				encoder.encodeSerializableValue(delegateSerializer, value.toArray())
		}
	}
}