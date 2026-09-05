package io.github.ayfri.kore.features.enchantments.effects.entity

import io.github.ayfri.kore.features.enchantments.values.Constant
import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.LevelBasedScope
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import kotlinx.serialization.Serializable

/**
 * Damages the enchanted item by [amount] durability points, breaking it when it runs out.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#damage_item
 *
 * @property amount The durability removed from the item.
 */
@Serializable
data class DamageItem(
	var amount: LevelBased = Constant(0f),
) : EntityEffect(), LevelBasedScope

/** Sets [DamageItem.amount] to [value], the same damage whatever the enchantment level is. */
fun DamageItem.amount(value: Float) {
	amount = constantLevelBased(value)
}

/** Sets [DamageItem.amount] to [value], the same damage whatever the enchantment level is. */
fun DamageItem.amount(value: Int) {
	amount = constantLevelBased(value)
}
