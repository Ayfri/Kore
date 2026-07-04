package io.github.ayfri.kore.features.worldgen.structures

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = MineshaftType.Companion.MineshaftTypeSerializer::class)
enum class MineshaftType {
	MESA,
	NORMAL;

	companion object {
		data object MineshaftTypeSerializer : LowercaseSerializer<MineshaftType>(entries)
	}
}
