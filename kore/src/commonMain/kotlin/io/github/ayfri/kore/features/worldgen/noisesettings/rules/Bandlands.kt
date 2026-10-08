package io.github.ayfri.kore.features.worldgen.noisesettings.rules

import kotlinx.serialization.Serializable

/**
 * Represents a bandlands material rule.
 */
@Serializable
data object Bandlands : MaterialRule()

/**
 * Appends a bandlands material rule.
 */
fun MaterialRulesScope.bandlands() = apply { rules += Bandlands }
