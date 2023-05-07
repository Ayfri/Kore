package features.loottables.entries

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import serializers.DirectPolymorphicSerializer

typealias LootEntry = @Serializable(LootEntrySerializer::class) LootEntrySurrogate

@Serializable
sealed interface LootEntrySurrogate /*{
	companion object {
		object LootEntrySerializer : KSerializer<LootEntrySerialized> by serializer() {
			override fun serialize(encoder: Encoder, value: LootEntrySerialized) {
				require(encoder is JsonEncoder) { "LootTable can only be serialized with Json" }

				encoder.encodeJsonElement(buildJsonObject {
					put("type", JsonPrimitive("minecraft:${value::class.simpleName!!.snakeCase()}"))
					Json.encodeToJsonElement(value).jsonObject.forEach { (key, value) ->
						if (key != "type") put(key, value)
					}
				})
			}
		}
	}
}
*/

object LootEntrySerializer : KSerializer<LootEntry> by DirectPolymorphicSerializer(LootEntrySurrogate.serializer())
