package io.github.ayfri.kore.features.enchantments.effects.value

import io.github.ayfri.kore.features.enchantments.values.Constant
import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import kotlinx.serialization.Serializable

/**
 * Multiplies the number the component computes by [factor].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#multiply
 *
 * @property factor The factor the value is multiplied by.
 */
@Serializable
data class Multiply(
	var factor: LevelBased = Constant(0f),
) : ValueEffect()
