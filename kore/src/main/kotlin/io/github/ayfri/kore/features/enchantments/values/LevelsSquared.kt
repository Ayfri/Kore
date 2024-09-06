package io.github.ayfri.kore.features.enchantments.values

import kotlinx.serialization.Serializable

@Serializable
data class LevelsSquared(
	var added: Int,
) : LevelBased()

fun levelsSquaredLevelBased(added: Int) = LevelsSquared(added)
