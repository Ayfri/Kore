package io.github.ayfri.kore.serializers

import io.github.ayfri.kore.arguments.numbers.toCanonicalString
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SealedSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.internal.AbstractPolymorphicSerializer
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonUnquotedLiteral

private val canonicalDouble = Double.serializer().canonicalNumbers()
private val canonicalFloat = Float.serializer().canonicalNumbers()

/**
 * Whether JSON encodes this strategy as is: a primitive other than float and double holds no number to rewrite, and the
 * serializers kotlinx.serialization generates for open and sealed classes only encode right when the [kotlinx.serialization.json.Json] itself runs them.
 * A float under such a default polymorphic serializer keeps the platform text, which Kore's families don't hit since
 * they use KSP dispatchers.
 */
@OptIn(InternalSerializationApi::class)
private val SerializationStrategy<*>.isLeftAlone
	get() = this is AbstractPolymorphicSerializer<*> ||
		descriptor.kind.let { it is PrimitiveKind && it != PrimitiveKind.DOUBLE && it != PrimitiveKind.FLOAT }

internal actual fun <T> SerializationStrategy<T>.canonicalNumbers(): SerializationStrategy<T> =
	if (isLeftAlone) this else object : SerializationStrategy<T> {
		override val descriptor = this@canonicalNumbers.descriptor
		override fun serialize(encoder: Encoder, value: T) = this@canonicalNumbers.serialize(encoder.canonicalNumbers(), value)
	}

private fun Encoder.canonicalNumbers() = if (this is JsonEncoder && this !is CanonicalNumberEncoder) CanonicalNumberEncoder(this) else this

/** Wraps a [JsonEncoder] so that everything nested in it is encoded with [canonicalNumbers]. */
@OptIn(ExperimentalSerializationApi::class, SealedSerializationApi::class)
private class CanonicalNumberEncoder(private val encoder: JsonEncoder) : JsonEncoder by encoder {
	override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder =
		CanonicalNumberComposite(encoder.beginCollection(descriptor, collectionSize))

	override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder = CanonicalNumberComposite(encoder.beginStructure(descriptor))

	override fun encodeDouble(value: Double) =
		if (value.isFinite()) encoder.encodeJsonElement(JsonUnquotedLiteral(value.toCanonicalString())) else encoder.encodeDouble(value)

	override fun encodeFloat(value: Float) =
		if (value.isFinite()) encoder.encodeJsonElement(JsonUnquotedLiteral(value.toCanonicalString())) else encoder.encodeFloat(value)

	override fun encodeInline(descriptor: SerialDescriptor) = encoder.encodeInline(descriptor).canonicalNumbers()

	override fun <T : Any> encodeNullableSerializableValue(serializer: SerializationStrategy<T>, value: T?) =
		if (value == null) encodeNull() else encodeSerializableValue(serializer, value)

	override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) =
		encoder.encodeSerializableValue(serializer.canonicalNumbers(), value)
}

@OptIn(ExperimentalSerializationApi::class)
private class CanonicalNumberComposite(private val composite: CompositeEncoder) : CompositeEncoder by composite {
	override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) =
		composite.encodeSerializableElement(descriptor, index, canonicalDouble, value)

	override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) =
		composite.encodeSerializableElement(descriptor, index, canonicalFloat, value)

	override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int) =
		composite.encodeInlineElement(descriptor, index).canonicalNumbers()

	override fun <T : Any> encodeNullableSerializableElement(descriptor: SerialDescriptor, index: Int, serializer: SerializationStrategy<T>, value: T?) =
		composite.encodeNullableSerializableElement(descriptor, index, serializer.canonicalNumbers(), value)

	override fun <T> encodeSerializableElement(descriptor: SerialDescriptor, index: Int, serializer: SerializationStrategy<T>, value: T) =
		composite.encodeSerializableElement(descriptor, index, serializer.canonicalNumbers(), value)
}
