package io.github.ayfri.kore.features.worldgen.dimensiontype

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = CardinalLight.Companion.CardinalLightSerializer::class)
enum class CardinalLight {
	DEFAULT,
	NETHER;

	companion object {
		data object CardinalLightSerializer : LowercaseSerializer<CardinalLight>(entries)
	}
}
