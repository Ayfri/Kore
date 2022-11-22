package arguments.enums

import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable(Dimension.Companion.DimensionSerializer::class)
enum class Dimension {
	OVERWORLD,
	THE_NETHER,
	THE_END;
	
	companion object {
		val values = values()
		
		object DimensionSerializer : LowercaseSerializer<Dimension>(values)
	}
}
