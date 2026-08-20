package io.github.ayfri.kore.features.enchantments

import kotlinx.serialization.Serializable

/**
 * The enchanting power an enchantment costs at a given level, `base + perLevelAboveFirst * (level - 1)`.
 *
 * Used by [Enchantment.minCost] and [Enchantment.maxCost] to bound the enchanting table power window the
 * enchantment can appear in. Unlike a
 * [level-based value][io.github.ayfri.kore.features.enchantments.values.LevelBased], both numbers are integers.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition
 *
 * @property base The cost at level 1.
 * @property perLevelAboveFirst The cost added by each level above the first.
 */
@Serializable
data class EnchantmentCost(
	var base: Int = 0,
	var perLevelAboveFirst: Int = 0,
)
