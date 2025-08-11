package io.github.ayfri.kore.features.itemmodifiers.formulas

import kotlinx.serialization.Serializable

/**
 * `apply_bonus` formula: 1 base item plus a binomially distributed bonus (n = level + extra, p = probability).
 * For example, a level 10 item with `extra = 1` and `probability = 0.5` will have a 50% chance of
 * receiving 1 bonus item (10 + 1 = 11 items total).
 *
 * Mirrors vanilla `binomial_with_bonus_count`.
 */
@Serializable
data class BinomialWithBonusCount(
	var extra: Int,
	var probability: Float,
) : Formula
