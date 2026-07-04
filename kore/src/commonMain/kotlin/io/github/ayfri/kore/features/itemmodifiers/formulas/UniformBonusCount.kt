package io.github.ayfri.kore.features.itemmodifiers.formulas

import kotlinx.serialization.Serializable

/** `apply_bonus` formula: uniform distribution from 0 to (level * bonusMultiplier). */
@Serializable
data class UniformBonusCount(
	var bonusMultiplier: Float,
) : Formula
