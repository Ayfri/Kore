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
 * A serializer that serializes to only one property's value if all other properties are null.
 *
 * Example:
 * ```kotlin
 * @KeepGeneratedSerializer
 * @Serializable(with = MyDataClass.Companion.MyDataClassSerializer::class)
 * data class MyDataClass(var myProperty: Int = 0, var myOtherProperty: Int? = null) {
 *     companion object {
 *         data object MyDataClassSerializer :
 *             SinglePropertySimplifierSerializer<MyDataClass>(generatedSerializer(), "myProperty")
 *     }
 * }
 * ```
 * If myOtherProperty is null, the JSON will be:
 * ```json
 * 0
 * ```
 * If myOtherProperty is not null, the JSON will be:
 * ```json
 * {
 *    "myProperty": 0,
 *    "myOtherProperty": 0
 * }
 * ```
 *
 * @param T The target class type.
 * @param delegate The plugin-generated structural serializer for [T] (use `@KeepGeneratedSerializer` + `generatedSerializer()`).
 * @param propertyName The serial name of the property to simplify to.
 */
open class SinglePropertySimplifierSerializer<T : Any>(
	private val delegate: KSerializer<T>,
	private val propertyName: String,
) : KSerializer<T> {
	override val descriptor get() = delegate.descriptor

	/** The value of the only entry of this object when its key is [key]. */
	private fun <E> Map<String, E>.singleValueAt(key: String) = entries.singleOrNull()?.takeIf { it.key == key }?.value

	override fun serialize(encoder: Encoder, value: T) = when (encoder) {
		is JsonEncoder -> {
			val obj = encoder.json.encodeToJsonElement(delegate, value).jsonObject
			encoder.encodeJsonElement(obj.singleValueAt(descriptor.jsonKey(encoder.json, propertyName)) ?: obj)
		}

		else -> encoder.nbtEncoder?.let { nbt ->
			val compound = encodeToNbtTag(delegate, value).nbtCompound
			nbt.encodeNbtTag(compound.singleValueAt(descriptor.snakeCaseKey(propertyName)) ?: compound)
		} ?: encoder.encodeSerializableValue(delegate, value)
	}

	override fun deserialize(decoder: Decoder): T = when (decoder) {
		is JsonDecoder -> {
			val element = decoder.decodeJsonElement()
			val obj = element as? JsonObject ?: JsonObject(mapOf(descriptor.jsonKey(decoder.json, propertyName) to element))
			decoder.json.decodeFromJsonElement(delegate, obj)
		}

		else -> decoder.nbtDecoder?.let { nbt ->
			val tag = nbt.decodeNbtTag()
			decodeFromNbtTag(delegate, tag as? NbtCompound ?: NbtCompound(mapOf(descriptor.snakeCaseKey(propertyName) to tag)))
		} ?: decoder.decodeSerializableValue(delegate)
	}
}
