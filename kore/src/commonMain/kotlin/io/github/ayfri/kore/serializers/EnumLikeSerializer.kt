package io.github.ayfri.kore.serializers

import io.github.ayfri.kore.utils.copyAllFrom
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.internal.AbstractPolymorphicSerializer
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.EmptySerializersModule

/**
 * Serializes a sealed hierarchy as Minecraft's "enum or typed object" shape, resolving every case automatically -
 * reflection-free, via the KSP-generated sealed serializer (see [GeneratedSealedSerializer]). No case list to
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
 *         @OptIn(InternalSerializationApi::class)
 *         data object MyModifierSerializer : EnumLikeSerializer<MyModifier>(myModifierSealedSerializer())
 *     }
 * }
 * ```
 */
@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
open class EnumLikeSerializer<T : Any>(generated: KSerializer<T>) : KSerializer<T> {
	private val polymorphic = generated as AbstractPolymorphicSerializer<T>

	private val baseName get() = polymorphic.descriptor.serialName

	override val descriptor = PrimitiveSerialDescriptor(baseName, PrimitiveKind.STRING)

	@OptIn(ExperimentalSerializationApi::class)
	private val serialNames: List<String> by lazy {
		val descriptor = polymorphic.descriptor
		if (descriptor.kind != PolymorphicKind.SEALED) return@lazy emptyList()
		descriptor.getElementDescriptor(1)
			.let { variants -> List(variants.elementsCount) { variants.getElementName(it) } }
	}

	private val serialNameByContentName by lazy { serialNames.associateBy(::defaultContentName) }

	private fun caseSerializer(contentName: String): DeserializationStrategy<T> {
		val serialName = serialNameByContentName[contentName] ?: contentName
		return polymorphic.findPolymorphicSerializerOrNull(ModuleOnlyDecoder(EmptySerializersModule()), serialName)
			?: error("No case '$contentName' in $baseName")
	}

	override fun serialize(encoder: Encoder, value: T) {
		require(encoder is JsonEncoder) { "$baseName can only be serialized as JSON." }

		val actual = polymorphic.findPolymorphicSerializer(encoder, value)
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
