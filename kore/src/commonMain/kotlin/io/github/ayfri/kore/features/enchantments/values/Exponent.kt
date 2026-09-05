package io.github.ayfri.kore.features.enchantments.values

import kotlinx.serialization.Serializable

/**
 * [base] raised to the power of [power].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#exponent
 *
 * @property base The number being raised.
 * @property power The exponent applied to [base].
 */
@Serializable
data class Exponent(
	var base: LevelBased,
	var power: LevelBased,
) : LevelBased(), LevelBasedScope

/** Creates an [Exponent] value worth `base ^ power`. */
fun LevelBasedScope.exponentLevelBased(base: LevelBased, power: LevelBased) = Exponent(base, power)

/** Creates an [Exponent] value worth `base ^ power`. */
fun LevelBasedScope.exponentLevelBased(base: Float, power: Float) = Exponent(Constant(base), Constant(power))

/** Creates an [Exponent] value worth `base ^ power`. */
fun LevelBasedScope.exponentLevelBased(base: Int, power: Int) = Exponent(Constant(base.toFloat()), Constant(power.toFloat()))
