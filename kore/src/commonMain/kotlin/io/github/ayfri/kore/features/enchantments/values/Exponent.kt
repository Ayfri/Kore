package io.github.ayfri.kore.features.enchantments.values

import kotlinx.serialization.Serializable

@Serializable
data class Exponent(
	var base: LevelBased,
	var power: LevelBased,
) : LevelBased()

fun exponentLevelBased(base: LevelBased, power: LevelBased) = Exponent(base, power)

fun exponentLevelBased(base: Int, power: Int) = Exponent(Constant(base), Constant(power))

fun exponentLevelBased(base: LevelBased, power: Int) = Exponent(base, Constant(power))

fun exponentLevelBased(base: Int, power: LevelBased) = Exponent(Constant(base), power)
