package io.github.ayfri.kore.features.enchantments.effects.entity

import io.github.ayfri.kore.features.enchantments.values.Constant
import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.LevelBasedScope
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import kotlinx.serialization.Serializable

/**
 * Adds [amount] hunger exhaustion to the affected player, doing nothing to non-player entities.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#apply_exhaustion
 *
 * @property amount The exhaustion added, one point being worth a fifth of a haunch of food.
 */
@Serializable
data class ApplyExhaustion(
	var amount: LevelBased = Constant(0f),
) : EntityEffect(), LevelBasedScope

/** Sets [ApplyExhaustion.amount] to [value], the same exhaustion whatever the enchantment level is. */
fun ApplyExhaustion.amount(value: Float) {
	amount = constantLevelBased(value)
}

/** Sets [ApplyExhaustion.amount] to [value], the same exhaustion whatever the enchantment level is. */
fun ApplyExhaustion.amount(value: Int) {
	amount = constantLevelBased(value)
}
