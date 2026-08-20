package io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions

import kotlinx.serialization.Serializable

/**
 * Represents a condition that checks whether the terrain slope at the current position is steep.
 */
@Serializable
data object Steep : SurfaceRuleCondition()
