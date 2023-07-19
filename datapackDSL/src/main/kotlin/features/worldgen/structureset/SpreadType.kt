package features.worldgen.structureset

import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable(SpreadType.Companion.SpreadTypeSerializer::class)
enum class SpreadType {
	LINEAR,
	TRIANGULAR;

	companion object {
		data object SpreadTypeSerializer : LowercaseSerializer<SpreadType>(entries)
	}
}
