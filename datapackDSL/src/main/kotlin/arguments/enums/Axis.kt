package arguments.enums

import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable(Axis.Companion.AxisSerializer::class)
enum class Axis {
	X,
	Y,
	Z;

	companion object {
		data object AxisSerializer : LowercaseSerializer<Axis>(entries)
	}
}
