package features.worldgen.noisesettings.rules.conditions

import kotlinx.serialization.Serializable

/**
 * Represents a condition that checks if an entity is above a preliminary surface.
 */
@Serializable
data object AbovePreliminarySurface : SurfaceRuleCondition()
