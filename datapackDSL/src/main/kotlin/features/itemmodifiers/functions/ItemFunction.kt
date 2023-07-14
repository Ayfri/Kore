package features.itemmodifiers.functions

import features.predicates.Predicate
import features.predicates.PredicateAsList
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import utils.snakeCase

@Serializable
sealed class ItemFunction(
	var conditions: PredicateAsList? = null
) {
	companion object {
		object ItemFunctionSerializer : KSerializer<ItemFunction> {
			override val descriptor = buildClassSerialDescriptor("ItemFunctionSurrogate") {
				element<String>("function")
				element<PredicateAsList>("conditions")
			}

			override fun deserialize(decoder: Decoder) = error("ItemFunctionSurrogate cannot be deserialized")

			override fun serialize(encoder: Encoder, value: ItemFunction) {
				require(encoder is JsonEncoder) { "ItemFunctionSurrogate can only be serialized with Json" }

				encoder.encodeJsonElement(buildJsonObject {
					put("function", "minecraft:${value::class.simpleName!!.snakeCase()}")

					val json = encoder.json.encodeToJsonElement(value)
					json.jsonObject.forEach { (key, value) ->
						if (key != "type") put(key, value)
					}
				})
			}
		}
	}
}

fun ItemFunction.conditions(block: Predicate.() -> Unit) {
	conditions = Predicate().apply(block)
}
