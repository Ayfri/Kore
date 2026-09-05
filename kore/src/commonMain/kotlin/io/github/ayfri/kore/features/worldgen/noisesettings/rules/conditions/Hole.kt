package io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions

import kotlinx.serialization.Serializable

/**
 * Represents a condition that checks whether the current position is inside a cave or other hole in the terrain.
 */
@Serializable
data object Hole : SurfaceRuleCondition()
