package io.github.ayfri.kore.features.advancements.types.entityspecific

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = TropicalFishVariants.Companion.TropicalFishVariantSerializer::class)
enum class TropicalFishVariants {
	KOB,
	SUNSTREAK,
	SNOOPER,
	DASHER,
	BRINELY,
	SPOTTY,
	FLOPPER,
	STRIPEY,
	GLITTER,
	BLOCKFISH,
	BETTY,
	CLAYFISH;

	companion object {
		data object TropicalFishVariantSerializer : LowercaseSerializer<TropicalFishVariants>(entries)
	}
}

@Serializable
data class TropicalFish(var variant: TropicalFishVariants? = null) : EntityTypeSpecific()
