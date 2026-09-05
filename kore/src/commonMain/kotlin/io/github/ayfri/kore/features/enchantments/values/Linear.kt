package io.github.ayfri.kore.features.enchantments.values

import kotlinx.serialization.Serializable

/**
 * A value growing linearly with the enchantment level, `base + perLevelAboveFirst * (level - 1)`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#linear
 *
 * @property base The value at level 1.
 * @property perLevelAboveFirst The amount added by each level above the first.
 */
@Serializable
data class Linear(
	var base: Float,
	var perLevelAboveFirst: Float,
) : LevelBased()

/** Creates a [Linear] value worth [base] at level 1, growing by [perLevelAboveFirst] per extra level. */
fun LevelBasedScope.linearLevelBased(base: Float, perLevelAboveFirst: Float) = Linear(base, perLevelAboveFirst)

/** Creates a [Linear] value worth [base] at level 1, growing by [perLevelAboveFirst] per extra level. */
fun LevelBasedScope.linearLevelBased(base: Int, perLevelAboveFirst: Int) = Linear(base.toFloat(), perLevelAboveFirst.toFloat())
