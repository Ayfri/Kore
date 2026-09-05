package io.github.ayfri.kore.features.enchantments.values

import kotlinx.serialization.Serializable

/**
 * [numerator] divided by [denominator], returning `0` when [denominator] evaluates to `0`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#fraction
 *
 * @property numerator The divided number.
 * @property denominator The divisor.
 */
@Serializable
data class Fraction(
	var numerator: LevelBased,
	var denominator: LevelBased,
) : LevelBased(), LevelBasedScope

/** Creates a [Fraction] value worth `numerator / denominator`. */
fun LevelBasedScope.fractionLevelBased(numerator: LevelBased, denominator: LevelBased) = Fraction(numerator, denominator)

/** Creates a [Fraction] value worth `numerator / denominator`. */
fun LevelBasedScope.fractionLevelBased(numerator: Float, denominator: Float) = Fraction(Constant(numerator), Constant(denominator))

/** Creates a [Fraction] value worth `numerator / denominator`. */
fun LevelBasedScope.fractionLevelBased(numerator: Int, denominator: Int) =
	Fraction(Constant(numerator.toFloat()), Constant(denominator.toFloat()))
