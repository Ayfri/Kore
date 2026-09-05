package io.github.ayfri.kore.features.enchantments.effects.builders

import kotlinx.serialization.Serializable

/**
 * The empty payload of a component that only has to be present to do its job, such as `prevent_armor_change`.
 *
 * Serialized as `{}`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#prevent_armor_change
 */
@Serializable
data object EmptyEffectBuilder : EffectBuilder()
