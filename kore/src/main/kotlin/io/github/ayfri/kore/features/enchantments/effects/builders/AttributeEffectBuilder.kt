package io.github.ayfri.kore.features.enchantments.effects.builders

import io.github.ayfri.kore.arguments.types.resources.AttributeModifierArgument
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.features.enchantments.effects.AttributeEffect
import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import io.github.ayfri.kore.generated.arguments.types.AttributeArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = AttributeEffectBuilder.Companion.AttributeEffectBuilderSerializer::class)
data class AttributeEffectBuilder(var effects: List<AttributeEffect> = emptyList()) : EffectBuilder() {
	companion object {
		data object AttributeEffectBuilderSerializer : InlineAutoSerializer<AttributeEffectBuilder>(AttributeEffectBuilder::class)
	}
}

fun AttributeEffectBuilder.attribute(
	id: AttributeModifierArgument,
	attribute: AttributeArgument,
	operation: AttributeModifierOperation,
	amount: LevelBased,
	block: AttributeEffect.() -> Unit = {},
) = apply { effects += AttributeEffect(id, attribute, operation, amount).apply(block) }

fun AttributeEffectBuilder.attribute(
	name: String,
	namespace: String = "minecraft",
	attribute: AttributeArgument,
	operation: AttributeModifierOperation,
	amount: LevelBased,
	block: AttributeEffect.() -> Unit = {},
) = apply { effects += AttributeEffect(AttributeModifierArgument(name, namespace), attribute, operation, amount).apply(block) }

fun AttributeEffectBuilder.attribute(
	id: AttributeModifierArgument,
	attribute: AttributeArgument,
	operation: AttributeModifierOperation,
	amount: Int,
	block: AttributeEffect.() -> Unit = {},
) = apply { effects += AttributeEffect(id, attribute, operation, constantLevelBased(amount)).apply(block) }

fun AttributeEffectBuilder.attribute(
	name: String,
	namespace: String = "minecraft",
	attribute: AttributeArgument,
	operation: AttributeModifierOperation,
	amount: Int,
	block: AttributeEffect.() -> Unit = {},
) = apply {
	effects += AttributeEffect(AttributeModifierArgument(name, namespace), attribute, operation, constantLevelBased(amount)).apply(block)
}
