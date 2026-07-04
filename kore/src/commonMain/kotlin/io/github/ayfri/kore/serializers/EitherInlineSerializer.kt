package io.github.ayfri.kore.serializers

import io.github.ayfri.kore.utils.nbt
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import net.benwoodworth.knbt.NbtDecoder
import net.benwoodworth.knbt.NbtEncoder
import net.benwoodworth.knbt.nbtCompound

/**
 * A serializer that inlines one of the specified properties if it's non-null.
 * If none of the specified properties are present (or if [inline] is false), it serializes as an object.
 *
 * @param T The target class type.
 * @param delegate The plugin-generated structural serializer for [T] (use `@KeepGeneratedSerializer` + `generatedSerializer()`).
 * @param propertyNamesToInline The serial names of the properties to check for inlining.
 * @param inline Whether to attempt to inline the first non-null property from [propertyNamesToInline].
 *
 * Example:
 * ```kotlin
 * @OptIn(ExperimentalSerializationApi::class)
 * @KeepGeneratedSerializer
 * @Serializable(with = MyClass.Companion.MyClassSerializer::class)
 * data class MyClass(val p1: String? = null, val p2: Int? = null) {
 *     companion object {
 *         data object MyClassSerializer : EitherInlineSerializer<MyClass>(generatedSerializer(), "p1", "p2")
 *     }
 * }
 * ```
 */
@OptIn(ExperimentalSerializationApi::class)
open class EitherInlineSerializer<T : Any>(
	private val delegate: KSerializer<T>,
	private vararg val propertyNamesToInline: String,
	private val inline: Boolean = true,
) : KSerializer<T> {
	override val descriptor get() = delegate.descriptor

	private fun jsonKey(json: Json, serialName: String): String {
		val index = delegate.descriptor.getElementIndex(serialName)
		return json.configuration.namingStrategy?.serialNameForJson(delegate.descriptor, index, serialName)
			?: serialName
	}

	override fun serialize(encoder: Encoder, value: T) {
		when (encoder) {
			is JsonEncoder -> {
				val obj = encoder.json.encodeToJsonElement(delegate, value).jsonObject
				if (inline) for (name in propertyNamesToInline) obj[jsonKey(encoder.json, name)]?.let {
					encoder.encodeJsonElement(it)
					return
				}
				encoder.encodeJsonElement(obj)
			}

			is NbtEncoder -> {
				val compound = encoder.nbt.encodeToNbtTag(delegate, value).nbtCompound
				if (inline) for (name in propertyNamesToInline) compound[name]?.let {
					encoder.encodeNbtTag(it)
					return
				}
				encoder.encodeNbtTag(compound)
			}

			else -> encoder.encodeSerializableValue(delegate, value)
		}
	}

	override fun deserialize(decoder: Decoder): T = when (decoder) {
		is JsonDecoder -> {
			val element = decoder.decodeJsonElement()
			val inlined = if (inline) propertyNamesToInline.firstNotNullOfOrNull { name ->
				val wrapped = buildJsonObject { put(jsonKey(decoder.json, name), element) }
				runCatching { decoder.json.decodeFromJsonElement(delegate, wrapped) }.getOrNull()
			} else null

			inlined ?: decoder.json.decodeFromJsonElement(delegate, element)
		}

		is NbtDecoder -> {
			val tag = decoder.decodeNbtTag()
			val inlined = if (inline) propertyNamesToInline.firstNotNullOfOrNull { name ->
				runCatching { decoder.nbt.decodeFromNbtTag(delegate, nbt { put(name, tag) }) }.getOrNull()
			} else null

			inlined ?: decoder.nbt.decodeFromNbtTag(delegate, tag)
		}

		else -> decoder.decodeSerializableValue(delegate)
	}
}
