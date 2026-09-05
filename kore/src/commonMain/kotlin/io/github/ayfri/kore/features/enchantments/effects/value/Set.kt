package io.github.ayfri.kore.features.enchantments.effects.value

import io.github.ayfri.kore.features.enchantments.values.Constant
import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import kotlinx.serialization.Serializable

/**
 * Replaces the number the component computes by [value].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#set
 *
 * @property value The number the component ends up with.
 */
@Serializable
data class Set(
	var value: LevelBased = Constant(0f),
) : ValueEffect()
