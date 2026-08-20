package io.github.ayfri.kore.features.enchantments.effects

import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.features.enchantments.values.Constant
import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.LevelBasedScope
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import io.github.ayfri.kore.generated.arguments.types.AttributeArgument
import io.github.ayfri.kore.generated.arguments.types.AttributeModifierArgument
import kotlinx.serialization.Serializable

/**
 * An attribute modifier applied to the holder of the enchantment for as long as it is equipped in one of its slots.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#attributes
 *
 * @property id The id of the modifier, used to remove it and to keep it from stacking with itself.
 * @property attribute The attribute the modifier applies to.
 * @property operation How [amount] is combined with the current value of the attribute.
 * @property amount The value of the modifier.
 */
@Serializable
data class AttributeEffect(
	var id: AttributeModifierArgument,
	var attribute: AttributeArgument,
	var operation: AttributeModifierOperation,
	var amount: LevelBased = Constant(0f),
) : EnchantmentEffect, LevelBasedScope

/** Sets [AttributeEffect.amount] to a constant [value], whatever the enchantment level is. */
fun AttributeEffect.amount(value: Float) {
	amount = constantLevelBased(value)
}

/** Sets [AttributeEffect.amount] to a constant [value], whatever the enchantment level is. */
fun AttributeEffect.amount(value: Int) {
	amount = constantLevelBased(value)
}
