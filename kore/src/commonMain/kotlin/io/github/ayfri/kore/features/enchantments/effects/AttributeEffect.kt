package io.github.ayfri.kore.features.enchantments.effects

import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.generated.arguments.types.AttributeArgument
import io.github.ayfri.kore.generated.arguments.types.AttributeModifierArgument
import kotlinx.serialization.Serializable

@Serializable
data class AttributeEffect(
	var id: AttributeModifierArgument,
	var attribute: AttributeArgument,
	var operation: AttributeModifierOperation,
	var amount: LevelBased,
) : EnchantmentEffect
