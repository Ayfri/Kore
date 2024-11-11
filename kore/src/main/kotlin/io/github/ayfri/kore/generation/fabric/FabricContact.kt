package io.github.ayfri.kore.generation.fabric

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@Serializable(with = FabricContact.Companion.ContactSerializer::class)
data class FabricContact(
	var email: String? = null,
	var irc: String? = null,
	var homepage: String? = null,
	var issues: String? = null,
	var sources: String? = null,
	var additional: Map<String, String> = emptyMap(),
) {
	companion object {
		data object ContactSerializer : KSerializer<FabricContact> {
			override val descriptor = JsonObject.serializer().descriptor

			override fun deserialize(decoder: Decoder) = error("Contact cannot be deserialized")

			override fun serialize(encoder: Encoder, value: FabricContact) {
				if (encoder !is JsonEncoder) error("Contact can only be serialized as Json")

				val json = buildJsonObject {
					value.email?.let { put("email", it) }
					value.irc?.let { put("irc", it) }
					value.homepage?.let { put("homepage", it) }
					value.issues?.let { put("issues", it) }
					value.sources?.let { put("sources", it) }
					value.additional.forEach { (key, value) -> put(key, value) }
				}

				encoder.encodeJsonElement(json)
			}
		}
	}
}
