package features.loottables.entries

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import utils.snakeCase

typealias LootEntry = @Serializable(LootEntrySurrogate.Companion.LootEntrySerializer::class) LootEntrySurrogate

typealias LootEntries = MutableList<LootEntry>

@Serializable
sealed interface LootEntrySurrogate {
	companion object {
		object LootEntrySerializer : KSerializer<LootEntry> {
			override val descriptor = buildClassSerialDescriptor("LootEntry") {
				element<String>("type")
			}

			override fun deserialize(decoder: Decoder) = error("LootEntry cannot be deserialized")

			override fun serialize(encoder: Encoder, value: LootEntry) {
				require(encoder is JsonEncoder) { "LootTable can only be serialized with Json" }

				encoder.encodeJsonElement(buildJsonObject {
					put("type", JsonPrimitive("minecraft:${value::class.simpleName!!.snakeCase()}"))
					encoder.json.encodeToJsonElement(value).jsonObject.forEach { (key, value) ->
						if (key != "type") put(key, value)
					}
				})
			}
		}
	}
}
