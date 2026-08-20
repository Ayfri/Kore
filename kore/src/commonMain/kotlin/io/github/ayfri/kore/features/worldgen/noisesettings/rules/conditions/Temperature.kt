package io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions

import kotlinx.serialization.Serializable

/**
 * Represents a condition that checks whether the current position is cold enough for frozen surface features
 * (snow, ice) to generate.
 */
@Serializable
data object Temperature : SurfaceRuleCondition()
