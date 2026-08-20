package io.github.ayfri.kore.features.enchantments.effects.entity

import kotlinx.serialization.Serializable

/**
 * Runs every effect of [effects] in order, so a single conditional effect can carry several of them.
 *
 * Built by the `allOf { }` builder of any entity effect scope rather than directly.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#all_of
 *
 * @property effects The effects run in order.
 */
@Serializable
data class AllOf(
	var effects: List<EntityEffect> = emptyList(),
) : EntityEffect()
