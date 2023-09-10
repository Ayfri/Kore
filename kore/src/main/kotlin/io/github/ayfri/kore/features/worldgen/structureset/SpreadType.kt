package io.github.ayfri.kore.features.worldgen.structureset

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(SpreadType.Companion.SpreadTypeSerializer::class)
enum class SpreadType {
	LINEAR,
	TRIANGULAR;

	companion object {
		data object SpreadTypeSerializer : LowercaseSerializer<SpreadType>(entries)
	}
}
