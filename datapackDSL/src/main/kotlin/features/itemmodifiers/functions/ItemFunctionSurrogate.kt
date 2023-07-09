package features.itemmodifiers.functions

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import utils.snakeCase

typealias ItemFunction = @Serializable(with = ItemFunctionSurrogate.Companion.ItemFunctionSerializer::class) ItemFunctionSurrogate

@Serializable
sealed interface ItemFunctionSurrogate {
	companion object {
		object ItemFunctionSerializer : KSerializer<ItemFunctionSurrogate> {
			override val descriptor = buildClassSerialDescriptor("ItemFunctionSurrogate") {
				element<String>("function")
			}

			override fun deserialize(decoder: Decoder) = error("ItemFunctionSurrogate cannot be deserialized")

			override fun serialize(encoder: Encoder, value: ItemFunctionSurrogate) {
				require(encoder is JsonEncoder) { "ItemFunctionSurrogate can only be serialized with Json" }

				// encode the type of the item function as the name of the class
				encoder.encodeJsonElement(buildJsonObject {
					put("function", "minecraft:${value::class.simpleName!!.snakeCase()}")
					val json = Json.encodeToJsonElement(value)
					json.jsonObject.forEach { (key, value) ->
						if (key != "type") put(key, value)
					}
				})
			}
		}
	}
}
