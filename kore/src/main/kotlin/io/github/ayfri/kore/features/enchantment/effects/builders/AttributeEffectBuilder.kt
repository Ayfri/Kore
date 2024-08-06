package io.github.ayfri.kore.features.enchantment.effects.builders

import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import io.github.ayfri.kore.arguments.types.resources.AttributeArgument
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.features.enchantment.effects.AttributeEffect
import io.github.ayfri.kore.features.enchantment.values.LevelBased
import io.github.ayfri.kore.features.enchantment.values.constantLevelBased
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer

@Serializable(with = AttributeEffectBuilder.Companion.AttributeEffectBuilderSerializer::class)
data class AttributeEffectBuilder(
	var effects: List<AttributeEffect> = emptyList(),
) : EffectBuilder() {
	companion object {
		data object AttributeEffectBuilderSerializer : InlineSerializer<AttributeEffectBuilder, List<AttributeEffect>>(
			kSerializer = ListSerializer(AttributeEffect.serializer()),
			property = AttributeEffectBuilder::effects
		)
	}
}

fun AttributeEffectBuilder.attribute(
	name: String,
	attribute: AttributeArgument,
	operation: AttributeModifierOperation,
	amount: LevelBased,
	uuid: UUIDArgument = UUIDArgument.random(),
	block: AttributeEffect.() -> Unit = {},
) = apply { effects += AttributeEffect(name, attribute, operation, amount, uuid).apply(block) }

fun AttributeEffectBuilder.attribute(
	name: String,
	attribute: AttributeArgument,
	operation: AttributeModifierOperation,
	amount: Int,
	uuid: UUIDArgument = UUIDArgument.random(),
	block: AttributeEffect.() -> Unit = {},
) = apply { effects += AttributeEffect(name, attribute, operation, constantLevelBased(amount), uuid).apply(block) }
