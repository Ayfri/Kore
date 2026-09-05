package io.github.ayfri.kore.features.enchantments.effects.value

import io.github.ayfri.kore.features.enchantments.values.Constant
import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import kotlinx.serialization.Serializable

/**
 * Rolls the number the component computes as a binomial trial, removing one unit per success with a [chance] probability each.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#remove_binomial
 *
 * @property chance The probability of each unit being removed, from `0` to `1`.
 */
@Serializable
data class RemoveBinomial(
	var chance: LevelBased = Constant(0f),
) : ValueEffect()
