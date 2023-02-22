package arguments.enums

import arguments.Argument
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Encoder
import serializers.LowercaseSerializer

@Serializable(Dimension.Companion.DimensionSerializer::class)
enum class Dimension : Argument.Dimension {
	OVERWORLD,
	THE_NETHER,
	THE_END;

	override val namespace = "minecraft"

	override fun asString() = "$namespace:${name.lowercase()}"

	companion object {
		val values = values()

		object DimensionSerializer : LowercaseSerializer<Dimension>(values) {
			override fun serialize(encoder: Encoder, value: Dimension) {
				println("DimensionSerializer.serialize")
				encoder.encodeString("minecraft:${value.name.lowercase()}")
			}
		}
	}
}
