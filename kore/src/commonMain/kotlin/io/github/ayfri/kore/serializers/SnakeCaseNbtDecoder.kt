package io.github.ayfri.kore.serializers

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import net.benwoodworth.knbt.NbtDecoder
import net.benwoodworth.knbt.asNbtDecoder

/** The knbt decoder behind [this], `null` when it doesn't read NBT. */
internal val Decoder.nbtDecoder get() = this as? NbtDecoder ?: (this as? SnakeCaseNbtDecoder)?.nbt

/** [this] reading its classes' properties like [snakeCased] writes them: snake_case, honoring [JsonSerialName]. */
internal fun <T> DeserializationStrategy<T>.snakeCased(): DeserializationStrategy<T> =
	if (isHandledByKnbt(descriptor)) this else object : DeserializationStrategy<T> {
		override val descriptor = this@snakeCased.descriptor
		override fun deserialize(decoder: Decoder) = this@snakeCased.deserialize(SnakeCaseNbtDecoder(decoder.asNbtDecoder()))
	}

/** Wraps knbt's [nbt] decoder, the twin of [SnakeCaseNbtEncoder]: it looks the keys of a compound up under the snake_case names. */
@OptIn(ExperimentalSerializationApi::class)
internal class SnakeCaseNbtDecoder(val nbt: NbtDecoder) : Decoder by nbt {
	override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder = SnakeCaseCompositeDecoder(nbt.beginStructure(descriptor), descriptor)

	override fun decodeInline(descriptor: SerialDescriptor) = SnakeCaseNbtDecoder(nbt.decodeInline(descriptor).asNbtDecoder())

	override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>) =
		if (deserializer.isHandledByKnbt(deserializer.descriptor)) nbt.decodeSerializableValue(deserializer) else deserializer.deserialize(this)

	override fun <T : Any> decodeNullableSerializableValue(deserializer: DeserializationStrategy<T?>) =
		if (deserializer.descriptor.isNullable || decodeNotNullMark()) decodeSerializableValue(deserializer) else decodeNull()
}

@OptIn(ExperimentalSerializationApi::class)
private class SnakeCaseCompositeDecoder(private val composite: CompositeDecoder, structure: SerialDescriptor) : CompositeDecoder by composite {
	private val snake = structure.withSnakeCaseNames()

	override fun decodeElementIndex(descriptor: SerialDescriptor) = composite.decodeElementIndex(snake)

	override fun decodeInlineElement(descriptor: SerialDescriptor, index: Int) =
		SnakeCaseNbtDecoder(composite.decodeInlineElement(descriptor, index).asNbtDecoder())

	override fun <T : Any> decodeNullableSerializableElement(descriptor: SerialDescriptor, index: Int, deserializer: DeserializationStrategy<T?>, previousValue: T?) =
		decodeInlineElement(descriptor, index).decodeNullableSerializableValue(deserializer)

	override fun <T> decodeSerializableElement(descriptor: SerialDescriptor, index: Int, deserializer: DeserializationStrategy<T>, previousValue: T?) =
		decodeInlineElement(descriptor, index).decodeSerializableValue(deserializer)
}
