package io.github.ayfri.kore.features.testenvironments.types

import kotlinx.serialization.Serializable
import io.github.ayfri.kore.features.testenvironments.enums.Weather as WeatherEnum

@Serializable
data class Weather(
	val weather: WeatherEnum
) : TestEnvironment()
