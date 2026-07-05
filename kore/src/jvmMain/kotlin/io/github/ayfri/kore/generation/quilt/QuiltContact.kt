package io.github.ayfri.kore.generation.quilt

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@Serializable(with = QuiltContact.Companion.ContactSerializer::class)
data class QuiltContact(
	var email: String? = null,
	var homepage: String? = null,
	var issues: String? = null,
	var sources: String? = null,
	var additional: Map<String, String> = emptyMap(),
) {
	companion object {
		data object ContactSerializer : KSerializer<QuiltContact> {
			override val descriptor = JsonObject.serializer().descriptor

			override fun deserialize(decoder: Decoder) = error("Contact cannot be deserialized")

			override fun serialize(encoder: Encoder, value: QuiltContact) {
				if (encoder !is JsonEncoder) error("Contact can only be serialized as Json")

				val json = buildJsonObject {
					value.email?.let { put("email", it) }
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
