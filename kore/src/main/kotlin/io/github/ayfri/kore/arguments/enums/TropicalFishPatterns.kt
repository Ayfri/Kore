package io.github.ayfri.kore.arguments.enums

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = TropicalFishPatterns.Companion.TropicalFishPatternSerializer::class)
enum class TropicalFishPatterns {
	BETTY,
	BLOCKFISH,
	BRINELY,
	CLAYFISH,
	DASHER,
	FLOPPER,
	GLITTER,
	KOB,
	SNOOPER,
	SPOTTY,
	STRIPEY,
	SUNSTREAK;

	companion object {
		data object TropicalFishPatternSerializer : LowercaseSerializer<TropicalFishPatterns>(entries)
	}
}
