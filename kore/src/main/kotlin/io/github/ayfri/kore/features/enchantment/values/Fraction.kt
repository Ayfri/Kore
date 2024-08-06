package io.github.ayfri.kore.features.enchantment.values

import kotlinx.serialization.Serializable

@Serializable
data class Fraction(
	var numerator: LevelBased,
	var denominator: LevelBased,
) : LevelBased()

fun fractionLevelBased(numerator: LevelBased, denominator: LevelBased) = Fraction(numerator, denominator)
fun fractionLevelBased(numerator: Int, denominator: Int) = Fraction(Constant(numerator), Constant(denominator))
fun fractionLevelBased(numerator: Int, denominator: LevelBased) = Fraction(Constant(numerator), denominator)
fun fractionLevelBased(numerator: LevelBased, denominator: Int) = Fraction(numerator, Constant(denominator))
