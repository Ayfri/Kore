package io.github.ayfri.kore.arguments.enums

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = VillagerVariants.Companion.VillagerVariantSerializer::class)
enum class VillagerVariants {
	DESERT,
	JUNGLE,
	PLAINS,
	SAVANNA,
	SNOW,
	SWAMP,
	TAIGA;

	companion object {
		data object VillagerVariantSerializer : LowercaseSerializer<VillagerVariants>(entries)
	}
}
