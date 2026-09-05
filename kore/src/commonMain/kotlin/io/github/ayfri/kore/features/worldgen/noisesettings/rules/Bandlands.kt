package io.github.ayfri.kore.features.worldgen.noisesettings.rules

import kotlinx.serialization.Serializable

/**
 * Represents a bandlands surface rule.
 */
@Serializable
data object Bandlands : SurfaceRule()

/**
 * Appends a bandlands surface rule.
 */
fun SurfaceRulesScope.bandlands() = apply { rules += Bandlands }
