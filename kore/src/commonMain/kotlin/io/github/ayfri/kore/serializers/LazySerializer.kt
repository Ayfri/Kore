package io.github.ayfri.kore.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Defers building the wrapped serializer until it's first used for encoding/decoding - [descriptor] is given upfront
 * instead, so it never forces [provider] to run.
 *
 * Needed when a `data object` serializer's supertype constructor would otherwise eagerly call another type's
 * `.serializer()` - fine normally, but a cycle between two *different* classes' companions (as opposed to a type
 * recursing into itself, which the compiler-generated serializer already handles lazily) deadlocks at class-init time
 * with an `ExceptionInInitializerError`: e.g. [SealedClassSerializer][kotlinx.serialization.SealedClassSerializer]
 * reads every subtype's `descriptor` eagerly to check for name collisions, which would otherwise force [provider] to
 * resolve the other class's serializer - including its own (still mid-init) descriptor - before it's ready.
 *
 * Example:
 * ```kotlin
 * data object FooSerializer : KSerializer<Foo> by LazySerializer(
 *     buildClassSerialDescriptor("FooSerializer"),
 *     { InlineAutoSerializer(Bar.serializer(), Foo::bar, ::Foo) },
 * )
 * ```
 */
class LazySerializer<T>(override val descriptor: SerialDescriptor, provider: () -> KSerializer<T>) : KSerializer<T> {
	private val delegate by lazy(provider)
	override fun serialize(encoder: Encoder, value: T) = delegate.serialize(encoder, value)
	override fun deserialize(decoder: Decoder): T = delegate.deserialize(decoder)
}
