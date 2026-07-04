package io.github.ayfri.kore.generation.quilt

import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.inlinableListSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class QuiltLicenseObject(
	var description: String? = null,
	var id: String,
	var name: String,
	var url: String,
)

@Serializable(with = QuiltLicense.Companion.QuiltLicenseSerializer::class)
data class QuiltLicense(
	var identifier: String? = null,
	var licenses: InlinableList<QuiltLicenseObject>? = null,
) {
	companion object {
		data object QuiltLicenseSerializer : KSerializer<QuiltLicense> {
			override val descriptor = QuiltLicenseObject.serializer().descriptor

			override fun deserialize(decoder: Decoder) = error("QuiltLicense cannot be deserialized.")

			override fun serialize(encoder: Encoder, value: QuiltLicense) {
				require(value.identifier == null && value.licenses == null) { "QuiltLicense must have either identifier or licenses." }

				if (value.identifier != null) encoder.encodeString(value.identifier!!)
				else encoder.encodeSerializableValue(inlinableListSerializer(QuiltLicenseObject.serializer()), value.licenses!!)
			}
		}
	}
}
