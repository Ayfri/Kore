package io.github.ayfri.kore.features.enchantments.effects.value

import kotlinx.serialization.Serializable

/**
 * Applies every effect of [effects] in order to the number the component computes.
 *
 * Built by the `allOf { }` builder of any value effect scope rather than directly.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#all_of
 *
 * @property effects The effects applied in order.
 */
@Serializable
data class AllOf(
	var effects: List<ValueEffect> = emptyList(),
) : ValueEffect()
