package io.github.ayfri.kore.serializers

import io.github.ayfri.kore.utils.decodeFromNbtTag
import io.github.ayfri.kore.utils.encodeToNbtTag
import io.github.ayfri.kore.utils.jsonKey
import io.github.ayfri.kore.utils.snakeCaseKey
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.nbtCompound

/**
 * A serializer that inlines one of the specified properties if it's non-null.
 * If none of the specified properties are present (or if [inline] is false), it serializes as an object.
 *
 * Example:
 * ```kotlin
 * @KeepGeneratedSerializer
 * @Serializable(with = MyClass.Companion.MyClassSerializer::class)
 * data class MyClass(val p1: String? = null, val p2: Int? = null) {
 *     companion object {
 *         data object MyClassSerializer : EitherInlineSerializer<MyClass>(generatedSerializer(), "p1", "p2")
 *     }
 * }
 * ```
 *
 * @param T The target class type.
 * @param delegate The plugin-generated structural serializer for [T] (use `@KeepGeneratedSerializer` + `generatedSerializer()`).
 * @param propertyNamesToInline The serial names of the properties to check for inlining.
 * @param inline Whether to attempt to inline the first non-null property from [propertyNamesToInline].
 * @param decodeOnly Serial names tried on decode after [propertyNamesToInline], but never inlined on encode. Lets a
 * property be read from its bare value (e.g. a list) while it is still written inside the object.
 * @param objectFallback Whether to decode the value as the full object when every inline attempt fails. When false, the last
 * inline failure is rethrown instead, so a bare value that is invalid for its property is reported rather than read as an object
 * that merely shares a property name with it, silently dropping the other keys.
 */
open class EitherInlineSerializer<T : Any>(
	private val delegate: KSerializer<T>,
	private vararg val propertyNamesToInline: String,
	private val inline: Boolean = true,
	private val decodeOnly: List<String> = emptyList(),
	private val objectFallback: Boolean = true,
) : KSerializer<T> {
	override val descriptor get() = delegate.descriptor

	private val namesToInline get() = if (inline) propertyNamesToInline.asList() else emptyList()

	private val namesToDecode get() = if (inline) propertyNamesToInline.asList() + decodeOnly else emptyList()

	override fun serialize(encoder: Encoder, value: T) = when (encoder) {
		is JsonEncoder -> {
			val obj = encoder.json.encodeToJsonElement(delegate, value).jsonObject
			encoder.encodeJsonElement(namesToInline.firstNotNullOfOrNull { obj[descriptor.jsonKey(encoder.json, it)] } ?: obj)
		}

		else -> encoder.nbtEncoder?.let { nbt ->
			val compound = encodeToNbtTag(delegate, value).nbtCompound
			nbt.encodeNbtTag(namesToInline.firstNotNullOfOrNull { compound[descriptor.snakeCaseKey(it)] } ?: compound)
		} ?: encoder.encodeSerializableValue(delegate, value)
	}

	override fun deserialize(decoder: Decoder): T = when (decoder) {
		is JsonDecoder -> {
			val element = decoder.decodeJsonElement()
			decodeInlined({ decoder.json.decodeFromJsonElement(delegate, element) }) { name ->
				val wrapped = JsonObject(mapOf(descriptor.jsonKey(decoder.json, name) to element))
				decoder.json.decodeFromJsonElement(delegate, wrapped)
			}
		}

		else -> decoder.nbtDecoder?.let { nbt ->
			val tag = nbt.decodeNbtTag()
			decodeInlined({ decodeFromNbtTag(delegate, tag) }) { name ->
				decodeFromNbtTag(delegate, NbtCompound(mapOf(descriptor.snakeCaseKey(name) to tag)))
			}
		} ?: decoder.decodeSerializableValue(delegate)
	}

	private inline fun decodeInlined(decodeObject: () -> T, decodeAs: (String) -> T): T {
		var lastFailure: Throwable? = null
		for (name in namesToDecode) {
			runCatching { decodeAs(name) }.onSuccess { return it }.onFailure { lastFailure = it }
		}

		val failure = lastFailure
		return if (objectFallback || failure == null) decodeObject() else throw failure
	}
}
