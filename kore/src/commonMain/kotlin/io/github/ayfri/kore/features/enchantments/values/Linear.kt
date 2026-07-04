package io.github.ayfri.kore.features.enchantments.values

import kotlinx.serialization.Serializable

@Serializable
data class Linear(
	var base: Int,
	var perLevelAboveFirst: Int,
) : LevelBased()

fun linearLevelBased(base: Int, perLevelAboveFirst: Int) = Linear(base, perLevelAboveFirst)
