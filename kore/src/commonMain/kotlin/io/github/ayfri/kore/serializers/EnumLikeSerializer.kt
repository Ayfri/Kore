package io.github.ayfri.kore.serializers

import io.github.ayfri.kore.utils.copyAllFrom
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

/**
 * Serializes a sealed hierarchy as Minecraft's "enum or typed object" shape, resolving every case automatically -
 * reflection-free, via the KSP-generated [SealedDispatcher] (see [GeneratedSealedSerializer]). No case list to
 * maintain.
 *
 * - Each `data object` leaf serializes to its bare snake_case name (e.g. `OVERRIDE` -> `"override"`).
 * - Each configurable leaf (a `data class`) serializes to `{ "type": <snake_case name>, ...its fields }`
 *   (e.g. `BlendToGray(...)` -> `{ "type": "blend_to_gray", ... }`). Every leaf must be `@Serializable`.
 *
 * ```kotlin
 * @GeneratedSealedSerializer
 * @Serializable(with = MyModifier.Companion.MyModifierSerializer::class)
 * sealed interface MyModifier {
 *     companion object {
 *         data object MyModifierSerializer : EnumLikeSerializer<MyModifier>(myModifierSealedSerializer())
 *     }
 * }
 * ```
 */
open class EnumLikeSerializer<T : Any>(private val dispatcher: SealedDispatcher<T>) : KSerializer<T> {
	private val baseName get() = dispatcher.serialName

	override val descriptor = PrimitiveSerialDescriptor(baseName, PrimitiveKind.STRING)

	private val serialNameByContentName = lazy { dispatcher.serializersBySerialName.keys.associateBy(::defaultContentName) }

	private fun caseSerializer(contentName: String): DeserializationStrategy<T> {
		val serialName = serialNameByContentName.value[contentName] ?: contentName
		return dispatcher.serializersBySerialName[serialName] ?: error("No case '$contentName' in $baseName")
	}

	override fun serialize(encoder: Encoder, value: T) {
		require(encoder is JsonEncoder) { "$baseName can only be serialized as JSON." }

		val actual = dispatcher.serializerOf(value)
		val name = defaultContentName(actual.descriptor.serialName)
		val body = encoder.json.encodeToJsonElement(actual, value)

		if (body is JsonObject && body.isEmpty()) {
			encoder.encodeString(name)
			return
		}

		encoder.encodeJsonElement(buildJsonObject {
			put("type", name)
			copyAllFrom(body as JsonObject, "type")
		})
	}

	override fun deserialize(decoder: Decoder): T {
		require(decoder is JsonDecoder) { "$baseName can only be deserialized from JSON." }

		return when (val element = decoder.decodeJsonElement()) {
			is JsonObject -> {
				val type = element.getValue("type").jsonPrimitive.content
				decoder.json.decodeFromJsonElement(
					caseSerializer(type),
					buildJsonObject { copyAllFrom(element, "type") })
			}

			else -> decoder.json.decodeFromJsonElement(
				caseSerializer(element.jsonPrimitive.content),
				buildJsonObject {})
		}
	}
}
