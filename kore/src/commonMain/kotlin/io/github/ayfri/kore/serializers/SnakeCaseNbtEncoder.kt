package io.github.ayfri.kore.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SealedSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.builtins.IntArraySerializer
import kotlinx.serialization.builtins.LongArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.internal.AbstractPolymorphicSerializer
import net.benwoodworth.knbt.NbtEncoder
import net.benwoodworth.knbt.asNbtEncoder

/** The knbt encoder behind [this], `null` when it doesn't write NBT. */
internal val Encoder.nbtEncoder get() = this as? NbtEncoder ?: (this as? SnakeCaseNbtEncoder)?.nbt

/** Whether knbt itself handles this (de)serializer (arrays, polymorphic, its own tags), so it never sees the snake_case wrappers. */
@OptIn(InternalSerializationApi::class)
internal fun Any.isHandledByKnbt(descriptor: SerialDescriptor) =
	this is AbstractPolymorphicSerializer<*> ||
		this == ByteArraySerializer() || this == IntArraySerializer() || this == LongArraySerializer() ||
		descriptor.serialName.startsWith("net.benwoodworth.knbt.")

/** [this] writing its classes' properties like the data pack JSON does: snake_case, honoring [JsonSerialName]. */
internal fun <T> SerializationStrategy<T>.snakeCased(): SerializationStrategy<T> =
	if (isHandledByKnbt(descriptor)) this else object : SerializationStrategy<T> {
		override val descriptor = this@snakeCased.descriptor
		override fun serialize(encoder: Encoder, value: T) = this@snakeCased.serialize(SnakeCaseNbtEncoder(encoder.asNbtEncoder()), value)
	}

/**
 * Wraps knbt's [nbt] encoder, which is sealed and writes the element names of the descriptors it is given.
 *
 * knbt itself encodes the serializers it special-cases (arrays, polymorphic, its own tags), so they never see this wrapper.
 */
@OptIn(ExperimentalSerializationApi::class)
internal class SnakeCaseNbtEncoder(val nbt: NbtEncoder) : Encoder by nbt {
	override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder =
		SnakeCaseComposite(nbt.beginCollection(descriptor, collectionSize), descriptor)

	override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder = SnakeCaseComposite(nbt.beginStructure(descriptor), descriptor)

	override fun encodeInline(descriptor: SerialDescriptor) = SnakeCaseNbtEncoder(nbt.encodeInline(descriptor).asNbtEncoder())

	override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) =
		if (serializer.isHandledByKnbt(serializer.descriptor)) nbt.encodeSerializableValue(serializer, value) else serializer.serialize(this, value)

	override fun <T : Any> encodeNullableSerializableValue(serializer: SerializationStrategy<T>, value: T?) =
		if (value == null) encodeNull() else encodeSerializableValue(serializer, value)
}

@OptIn(ExperimentalSerializationApi::class)
private class SnakeCaseComposite(private val composite: CompositeEncoder, private val structure: SerialDescriptor) : CompositeEncoder by composite {
	private val snake = structure.withSnakeCaseNames()

	private fun SerialDescriptor.renamed() = if (this === structure) snake else this

	override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) =
		composite.encodeBooleanElement(descriptor.renamed(), index, value)

	override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) =
		composite.encodeByteElement(descriptor.renamed(), index, value)

	override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) =
		composite.encodeCharElement(descriptor.renamed(), index, value)

	override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) =
		composite.encodeDoubleElement(descriptor.renamed(), index, value)

	override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) =
		composite.encodeFloatElement(descriptor.renamed(), index, value)

	override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int) =
		SnakeCaseNbtEncoder(composite.encodeInlineElement(descriptor.renamed(), index).asNbtEncoder())

	override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) =
		composite.encodeIntElement(descriptor.renamed(), index, value)

	override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) =
		composite.encodeLongElement(descriptor.renamed(), index, value)

	/** NBT has no null, so an unset element is left out: the JS serialization plugin writes the properties a class inherits from another module without checking them. */
	override fun <T : Any> encodeNullableSerializableElement(descriptor: SerialDescriptor, index: Int, serializer: SerializationStrategy<T>, value: T?) {
		if (value != null) encodeInlineElement(descriptor, index).encodeSerializableValue(serializer, value)
	}

	override fun <T> encodeSerializableElement(descriptor: SerialDescriptor, index: Int, serializer: SerializationStrategy<T>, value: T) =
		encodeInlineElement(descriptor, index).encodeSerializableValue(serializer, value)

	override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) =
		composite.encodeShortElement(descriptor.renamed(), index, value)

	override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) =
		composite.encodeStringElement(descriptor.renamed(), index, value)
}

/** [this] with its element names in snake_case when it describes a class, the only kind knbt reads or writes names of. */
internal fun SerialDescriptor.withSnakeCaseNames() = if (kind == StructureKind.CLASS) SnakeCaseDescriptor(this) else this

@OptIn(SealedSerializationApi::class)
private class SnakeCaseDescriptor(private val original: SerialDescriptor) : SerialDescriptor by original {
	private val indexes by lazy { (0 until elementsCount).associateBy(::getElementName) }

	override fun getElementIndex(name: String) = indexes[name] ?: CompositeDecoder.UNKNOWN_NAME

	override fun getElementName(index: Int) = JsonNamingSnakeCaseStrategy.serialNameForJson(original, index, original.getElementName(index))
}
