package io.github.ayfri.kore.features.advancements.types.entityspecific

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = HorseVariants.Companion.HorseVariantSerializer::class)
enum class HorseVariants {
	BLACK,
	BROWN,
	CHESTNUT,
	CREAMY,
	DARK_BROWN,
	GRAY,
	WHITE;

	companion object {
		data object HorseVariantSerializer : LowercaseSerializer<HorseVariants>(entries)
	}
}

@Serializable
data class Horse(var variant: HorseVariants? = null) : EntityTypeSpecific()
