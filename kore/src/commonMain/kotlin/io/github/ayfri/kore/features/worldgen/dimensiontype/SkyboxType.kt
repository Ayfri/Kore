package io.github.ayfri.kore.features.worldgen.dimensiontype

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = SkyboxType.Companion.SkyboxTypeSerializer::class)
enum class SkyboxType {
	NONE,
	OVERWORLD,
	END;

	companion object {
		data object SkyboxTypeSerializer : LowercaseSerializer<SkyboxType>(entries)
	}
}
