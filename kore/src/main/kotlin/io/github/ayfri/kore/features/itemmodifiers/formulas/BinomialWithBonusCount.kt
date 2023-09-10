package io.github.ayfri.kore.features.itemmodifiers.formulas

import kotlinx.serialization.Serializable

@Serializable
data class BinomialWithBonusCount(
	var extra: Int,
	var probability: Float,
) : Formula
