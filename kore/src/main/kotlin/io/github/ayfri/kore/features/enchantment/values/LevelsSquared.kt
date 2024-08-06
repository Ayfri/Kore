package io.github.ayfri.kore.features.enchantment.values

import kotlinx.serialization.Serializable

@Serializable
data class LevelsSquared(
	var added: Int,
) : LevelBased()

fun levelsSquaredLevelBased(added: Int) = LevelsSquared(added)
