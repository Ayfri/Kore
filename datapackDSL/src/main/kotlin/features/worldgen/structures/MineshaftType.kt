package features.worldgen.structures

import serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = MineshaftType.Companion.MineshaftTypeSerializer::class)
enum class MineshaftType {
	MESA,
	NORMAL;

	companion object {
		data object MineshaftTypeSerializer : LowercaseSerializer<MineshaftType>(entries)
	}
}
