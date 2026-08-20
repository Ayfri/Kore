package io.github.ayfri.kore.features.enchantments.effects.value

import io.github.ayfri.kore.features.enchantments.values.Constant
import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import kotlinx.serialization.Serializable

/**
 * Adds [value] to the number the component computes.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#add
 *
 * @property value The amount added.
 */
@Serializable
data class Add(
	var value: LevelBased = Constant(0f),
) : ValueEffect()
