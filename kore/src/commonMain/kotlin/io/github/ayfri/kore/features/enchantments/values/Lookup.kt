package io.github.ayfri.kore.features.enchantments.values

import kotlinx.serialization.Serializable

@Serializable
data class Lookup(
	var values: List<Int> = emptyList(),
	var fallback: LevelBased = Constant(0),
) : LevelBased()

fun lookupLevelBased(vararg values: Int, fallback: LevelBased = Constant(0)) = Lookup(values.toList(), fallback)
fun lookupLevelBased(vararg values: Int, fallback: Int) = Lookup(values.toList(), Constant(fallback))
