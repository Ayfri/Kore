package io.github.ayfri.kore.serializers

import io.github.ayfri.kore.arguments.Argument
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive
import net.benwoodworth.knbt.NbtString

/**
 * Serializes a reference wrapping an `XxxOrTagArgument` as the bare string of its [property], and reads back `"ns:id"`
 * with [id] and `"#ns:tag"` with [tag] into a reference built with [factory]. It only accepts a string, so a number or
 * an object is left to the other cases of a sealed family decoding bare values.
 *
 * ```kotlin
 * data object PredicateReferenceSerializer : IdOrTagReferenceSerializer<PredicateReference, PredicateOrTagArgument>(
 *     PredicateReference::id,
 *     ::PredicateReference,
 *     { name, namespace -> PredicateArgument(name, namespace) },
 *     { name, namespace -> PredicateTagArgument(name, namespace) },
 *     "predicate_reference",
 * )
 * ```
 */
open class IdOrTagReferenceSerializer<R, A : Argument>(
	private val property: (R) -> A,
	private val factory: (A) -> R,
	private val id: (name: String, namespace: String) -> A,
	private val tag: (name: String, namespace: String) -> A,
	serialName: String,
) : KSerializer<R> {
	override val descriptor = PrimitiveSerialDescriptor(serialName, PrimitiveKind.STRING)

	override fun deserialize(decoder: Decoder): R {
		val nbt = decoder.nbtDecoder
		val raw = when {
			decoder is JsonDecoder -> (decoder.decodeJsonElement() as? JsonPrimitive)?.takeIf { it.isString }?.content
			nbt != null -> (nbt.decodeNbtTag() as? NbtString)?.value
			else -> decoder.decodeString()
		} ?: error("An ID or a tag must be a string.")

		val (name, namespace) = raw.removePrefix("#").splitNamespacedId()
		return factory(if (raw.startsWith('#')) tag(name, namespace) else id(name, namespace))
	}

	override fun serialize(encoder: Encoder, value: R) = encoder.encodeString(property(value).asString())
}
