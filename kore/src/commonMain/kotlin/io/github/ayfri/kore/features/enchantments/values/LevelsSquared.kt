package io.github.ayfri.kore.features.enchantments.values

import kotlinx.serialization.Serializable

/**
 * A value worth `level * level + added`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#levels_squared
 *
 * @property added The amount added to the squared level.
 */
@Serializable
data class LevelsSquared(
	var added: Float,
) : LevelBased()

/** Creates a [LevelsSquared] value worth `level * level + added`. */
fun LevelBasedScope.levelsSquaredLevelBased(added: Float) = LevelsSquared(added)

/** Creates a [LevelsSquared] value worth `level * level + added`. */
fun LevelBasedScope.levelsSquaredLevelBased(added: Int) = LevelsSquared(added.toFloat())
