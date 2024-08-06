package io.github.ayfri.kore.features.enchantment.effects

import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import io.github.ayfri.kore.arguments.types.resources.AttributeArgument
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.features.enchantment.values.LevelBased
import kotlinx.serialization.Serializable

@Serializable
data class AttributeEffect(
	var name: String,
	var attribute: AttributeArgument,
	var operation: AttributeModifierOperation,
	var amount: LevelBased,
	var uuid: UUIDArgument = UUIDArgument.random(),
) : EnchantmentEffect
