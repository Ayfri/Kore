package arguments.enums

import arguments.Argument
import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable(Dimension.Companion.DimensionSerializer::class)
enum class Dimension : Argument.Dimension {
	OVERWORLD,
	THE_NETHER,
	THE_END;

	override val namespace = "minecraft"

	override fun asId() = "$namespace:${name.lowercase()}"

	companion object {
		data object DimensionSerializer : LowercaseSerializer<Dimension>(entries)
	}
}
