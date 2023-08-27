package arguments.enums

import arguments.types.resources.worldgen.DimensionArgument
import serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(Dimension.Companion.DimensionSerializer::class)
enum class Dimension : DimensionArgument {
	OVERWORLD,
	THE_NETHER,
	THE_END;

	override val namespace = "minecraft"

	override fun asId() = "$namespace:${name.lowercase()}"

	companion object {
		data object DimensionSerializer : LowercaseSerializer<Dimension>(entries)
	}
}
