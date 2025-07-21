package io.github.ayfri.kore.features.testenvironments.types

import io.github.ayfri.kore.features.testenvironments.enums.Weather as WeatherEnum
import kotlinx.serialization.Serializable

@Serializable
data class Weather(
	val weather: WeatherEnum
) : TestEnvironment()
