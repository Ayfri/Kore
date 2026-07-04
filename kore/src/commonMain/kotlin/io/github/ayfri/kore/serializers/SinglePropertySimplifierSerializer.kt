package io.github.ayfri.kore.serializers

import io.github.ayfri.kore.utils.nbt
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtDecoder
import net.benwoodworth.knbt.NbtEncoder
import net.benwoodworth.knbt.nbtCompound

/**
 * A serializer that serializes to only one property's value if all other properties are null.
 *
 * @param T The target class type.
 * @param delegate The plugin-generated structural serializer for [T] (use `@KeepGeneratedSerializer` + `generatedSerializer()`).
 * @param propertyName The serial name of the property to simplify to.
 *
 * Example:
 * ```kotlin
 * @OptIn(ExperimentalSerializationApi::class)
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
 */
@OptIn(ExperimentalSerializationApi::class)
open class SinglePropertySimplifierSerializer<T : Any>(
	private val delegate: KSerializer<T>,
	private val propertyName: String,
) : KSerializer<T> {
	override val descriptor get() = delegate.descriptor

	private fun jsonKey(json: Json): String {
		val index = delegate.descriptor.getElementIndex(propertyName)
		return json.configuration.namingStrategy?.serialNameForJson(delegate.descriptor, index, propertyName)
			?: propertyName
	}

	override fun serialize(encoder: Encoder, value: T) = when (encoder) {
		is JsonEncoder -> {
			val obj = encoder.json.encodeToJsonElement(delegate, value).jsonObject
			val key = jsonKey(encoder.json)
			if (key in obj && obj.keys.all { it == key }) encoder.encodeJsonElement(obj.getValue(key))
			else encoder.encodeJsonElement(obj)
		}

		is NbtEncoder -> {
			val compound = encoder.nbt.encodeToNbtTag(delegate, value).nbtCompound
			if (propertyName in compound && compound.keys.all { it == propertyName }) encoder.encodeNbtTag(
				compound.getValue(
					propertyName
				)
			)
			else encoder.encodeNbtTag(compound)
		}

		else -> encoder.encodeSerializableValue(delegate, value)
	}

	override fun deserialize(decoder: Decoder): T = when (decoder) {
		is JsonDecoder -> {
			val element = decoder.decodeJsonElement()
			val obj = element as? JsonObject ?: buildJsonObject { put(jsonKey(decoder.json), element) }
			decoder.json.decodeFromJsonElement(delegate, obj)
		}

		is NbtDecoder -> {
			val tag = decoder.decodeNbtTag()
			val compound = tag as? NbtCompound ?: nbt { put(propertyName, tag) }
			decoder.nbt.decodeFromNbtTag(delegate, compound)
		}

		else -> decoder.decodeSerializableValue(delegate)
	}
}
