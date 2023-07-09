package features.itemmodifiers

import features.itemmodifiers.functions.ItemFunction
import features.itemmodifiers.functions.ItemFunctionSurrogate
import features.loottables.LootPoolSerializer
import features.predicates.Predicate
import features.predicates.conditions.PredicateCondition
import features.predicates.conditions.PredicateConditions
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import java.util.concurrent.locks.Condition

@Serializable(with = ItemModifierEntry.Companion.ItemModifierEntrySerializer::class)
data class ItemModifierEntry(
	var function: ItemFunction,
	var conditions: PredicateConditions? = null,
) {
	companion object {
		data object ItemModifierEntrySerializer : KSerializer<ItemModifierEntry> {
			private val WORKAROUND_FIELD = "______do_not_use_this_field______${LootPoolSerializer.hashCode()}"
			private val workaroundJson = Json {
				classDiscriminator = WORKAROUND_FIELD
			}


			override val descriptor = buildClassSerialDescriptor("ItemModifierEntry") {
				element<String>("function")
				element<Condition>("conditions", isOptional = true)
			}

			override fun deserialize(decoder: Decoder) = error("ItemModifierEntry cannot be deserialized")

			override fun serialize(encoder: Encoder, value: ItemModifierEntry) {
				require(encoder is JsonEncoder) { "ItemModifier serializer can only be used with JsonEncoder" }

				encoder.encodeJsonElement(buildJsonObject {
					val json = Json.encodeToJsonElement(ItemFunctionSurrogate.Companion.ItemFunctionSerializer, value.function)
					json.jsonObject.forEach { (key, value) ->
						put(key, value)
					}

					value.conditions?.let {
						val element = workaroundJson.encodeToJsonElement(ListSerializer(PredicateCondition.serializer()), it)
						val resultElement = element.jsonArray.map { jsonElement ->
							JsonObject(jsonElement.jsonObject.filterKeys { key -> key != WORKAROUND_FIELD })
						}

						put("conditions", JsonArray(resultElement))
					}
				})
			}
		}
	}
}

fun ItemModifierEntry.conditions(init: Predicate.() -> Unit) {
	conditions = Predicate().apply(init).predicateConditions
}
