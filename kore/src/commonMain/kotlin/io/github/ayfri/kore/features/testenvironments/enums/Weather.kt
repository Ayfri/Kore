package io.github.ayfri.kore.features.testenvironments.enums

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/**
 * Represents weather conditions for test environments.
 */
@Serializable(Weather.Companion.WeatherSerializer::class)
enum class Weather {
	/** Clear weather with no precipitation */
	CLEAR,

	/** Rainy weather */
	RAIN,

	/** Thunderstorm weather */
	THUNDER;

	companion object {
		data object WeatherSerializer : LowercaseSerializer<Weather>(entries)
	}
}
