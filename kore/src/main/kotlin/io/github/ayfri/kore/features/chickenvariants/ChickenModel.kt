package io.github.ayfri.kore.features.chickenvariants

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ChickenModel.Companion.ChickenModelSerializer::class)
enum class ChickenModel {
	COLD,
	NORMAL;

	companion object {
		data object ChickenModelSerializer : LowercaseSerializer<ChickenModel>(entries)
	}
}
