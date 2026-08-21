package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import kotlinx.serialization.Serializable

/**
 * Passes when the current weather matches [raining] and [thundering].
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/predicates
 * Minecraft Wiki: [Predicate - weather_check](https://minecraft.wiki/w/Predicate#weather_check)
 */
@Serializable
data class WeatherCheck(
	var raining: Boolean? = null,
	var thundering: Boolean? = null,
) : PredicateCondition()

/** Adds a [WeatherCheck] condition. */
fun Predicate.weatherCheck(raining: Boolean? = null, thundering: Boolean? = null) {
	predicateConditions += WeatherCheck(raining, thundering)
}
