package io.github.ayfri.kore.serializers

import kotlinx.serialization.KSerializer

/** The simple class name generated dispatchers switch on, `Class.getSimpleName` on the JVM: `KClass.simpleName` makes two native calls there. */
internal expect fun subtypeName(value: Any): String?

/**
 * Resolves the serializers of a sealed family's subtypes on demand, so serializing a value only loads its own subtype
 * instead of the whole family. kore-ksp generates one per [GeneratedSealedSerializer] family, which
 * [NamespacedPolymorphicSerializer] and [EnumLikeSerializer] take.
 *
 * ```kotlin
 * object : SealedDispatcher<Animal>("Animal") {
 *     override fun serializerOf(value: Animal) = when (value) {
 *         is Cat -> Cat.serializer()
 *         is Dog -> Dog.serializer()
 *     } as KSerializer<Animal>
 *
 *     override fun serializers() = listOf(Cat.serializer(), Dog.serializer())
 * }
 * ```
 *
 * @property serialName the family's name, used in error messages and descriptors
 */
abstract class SealedDispatcher<T : Any>(val serialName: String) {
	/** The serializer of [value]'s subtype. */
	abstract fun serializerOf(value: T): KSerializer<T>

	/** Every subtype's serializer, in the order bare values are tried when decoding. */
	protected abstract fun serializers(): List<KSerializer<out T>>

	/** Every subtype's serializer by serial name, loading the whole family on first access: only decoding needs it. */
	val serializersBySerialName: Map<String, KSerializer<out T>> by lazy {
		val serializers = serializers()
		serializers.associateBy { it.descriptor.serialName }.also { bySerialName ->
			check(bySerialName.size == serializers.size) {
				val duplicates = serializers.groupBy { it.descriptor.serialName }.filterValues { it.size > 1 }.keys
				"Several subtypes of '$serialName' share the serial names $duplicates."
			}
		}
	}
}
