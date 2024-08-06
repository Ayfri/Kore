package io.github.ayfri.kore.features.enchantment.values

import kotlinx.serialization.Serializable

@Serializable
data class Clamped(
	var value: LevelBased,
	var min: Double,
	var max: Double,
) : LevelBased()

fun clampedLevelBased(value: Int, min: Double, max: Double) = Clamped(Constant(value), min, max)

fun clampedLevelBased(value: LevelBased, min: Double, max: Double) = Clamped(value, min, max)
