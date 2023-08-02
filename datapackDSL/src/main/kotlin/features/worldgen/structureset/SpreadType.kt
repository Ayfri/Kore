package features.worldgen.structureset

import serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(SpreadType.Companion.SpreadTypeSerializer::class)
enum class SpreadType {
	LINEAR,
	TRIANGULAR;

	companion object {
		data object SpreadTypeSerializer : LowercaseSerializer<SpreadType>(entries)
	}
}
