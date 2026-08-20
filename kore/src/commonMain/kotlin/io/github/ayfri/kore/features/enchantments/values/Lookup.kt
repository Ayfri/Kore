package io.github.ayfri.kore.features.enchantments.values

import kotlinx.serialization.Serializable

/**
 * A value read from [values] at index `level - 1`, falling back to [fallback] for levels beyond the list.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#lookup
 *
 * @property values The value of each level, starting at level 1.
 * @property fallback The value used when the level is greater than the size of [values].
 */
@Serializable
data class Lookup(
	var values: List<Float> = emptyList(),
	var fallback: LevelBased = Constant(0f),
) : LevelBased(), LevelBasedScope

/** Creates a [Lookup] listing the value of each level, using [fallback] beyond the last one. */
fun LevelBasedScope.lookupLevelBased(vararg values: Float, fallback: LevelBased) = Lookup(values.toList(), fallback)

/** Creates a [Lookup] listing the value of each level, using the constant [fallback] beyond the last one. */
fun LevelBasedScope.lookupLevelBased(vararg values: Float, fallback: Float) = Lookup(values.toList(), Constant(fallback))

/** Creates a [Lookup] listing the value of each level, using the constant [fallback] beyond the last one. */
fun LevelBasedScope.lookupLevelBased(vararg values: Int, fallback: Int) = Lookup(values.map(Int::toFloat), Constant(fallback.toFloat()))
