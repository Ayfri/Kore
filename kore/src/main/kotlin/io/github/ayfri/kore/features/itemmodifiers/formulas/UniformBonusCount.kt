package io.github.ayfri.kore.features.itemmodifiers.formulas

import kotlinx.serialization.Serializable

@Serializable
data class UniformBonusCount(
	var bonusMultiplier: Float,
) : Formula
