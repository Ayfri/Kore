package io.github.ayfri.kore.serializers

import io.github.ayfri.kore.utils.copyAllFrom
import io.github.ayfri.kore.utils.serializerFor
import io.github.ayfri.kore.utils.snakeCase
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

/**
 * Serializes a sealed hierarchy as Minecraft's "enum or typed object" shape, resolving every case automatically by
 * reflection - no case list to maintain.
 *
 * - Each `data object` leaf serializes to its bare lowercase name (e.g. `OVERRIDE` -> `"override"`).
 * - Each configurable leaf (a `data class`) serializes to `{ "type": <snake_case name>, ...its fields }`
 *   (e.g. `BlendToGray(...)` -> `{ "type": "blend_to_gray", ... }`). Such leaves must be `@Serializable`.
 *
 * Example:
 * ```kotlin
 * data object MyModifierSerializer : EnumLikeSerializer<MyModifier>(MyModifier::class)
 *
 * @Serializable(with = MyModifierSerializer::class)
 * sealed interface MyModifier
 * ```
 */
open class EnumLikeSerializer<T : Any>(private val klass: KClass<T>) : KSerializer<T> {
	override val descriptor = PrimitiveSerialDescriptor(klass.qualifiedName ?: "EnumLike", PrimitiveKind.STRING)

	private fun KClass<*>.leaves(): List<KClass<*>> =
		if (sealedSubclasses.isEmpty()) listOf(this) else sealedSubclasses.flatMap { it.leaves() }

	private val leaves by lazy { klass.leaves().distinct() }

	@Suppress("UNCHECKED_CAST")
	private val objectsByName by lazy {
		leaves.mapNotNull { it.objectInstance as T? }.associateBy { it::class.simpleName!!.lowercase() }
	}

	override fun serialize(encoder: Encoder, value: T) {
		require(encoder is JsonEncoder) { "${klass.simpleName} can only be serialized as JSON." }
		val name = value::class.simpleName!!

		if (value::class.objectInstance != null) {
			encoder.encodeString(name.lowercase())
			return
		}

		val body = encoder.json.encodeToJsonElement(encoder.serializersModule.serializerFor(value), value) as JsonObject
		encoder.encodeJsonElement(buildJsonObject {
			put("type", name.snakeCase())
			copyAllFrom(body, "type")
		})
	}

	@Suppress("UNCHECKED_CAST")
	override fun deserialize(decoder: Decoder): T {
		require(decoder is JsonDecoder) { "${klass.simpleName} can only be deserialized from JSON." }

		return when (val element = decoder.decodeJsonElement()) {
			is JsonObject -> {
				val type = element.getValue("type").jsonPrimitive.content
				val subclass = leaves.first { it.objectInstance == null && it.simpleName!!.snakeCase() == type }
				val serializer = decoder.json.serializersModule.serializer(subclass.createType()) as KSerializer<T>
				decoder.json.decodeFromJsonElement(serializer, buildJsonObject { copyAllFrom(element, "type") })
			}

			else -> objectsByName.getValue(element.jsonPrimitive.content)
		}
	}
}
