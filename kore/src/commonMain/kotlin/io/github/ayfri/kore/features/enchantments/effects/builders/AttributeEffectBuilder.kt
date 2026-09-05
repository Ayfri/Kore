package io.github.ayfri.kore.features.enchantments.effects.builders

import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.features.enchantments.effects.AttributeEffect
import io.github.ayfri.kore.features.enchantments.values.Constant
import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.LevelBasedScope
import io.github.ayfri.kore.generated.arguments.types.AttributeArgument
import io.github.ayfri.kore.generated.arguments.types.AttributeModifierArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * The list of attribute modifiers of the `attributes` component, applied while the enchanted item sits in one of the
 * slots of the enchantment.
 *
 * Serialized as the bare list.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#attributes
 */
@Serializable(with = AttributeEffectBuilder.Companion.AttributeEffectBuilderSerializer::class)
data class AttributeEffectBuilder(var effects: List<AttributeEffect> = emptyList()) : EffectBuilder(), LevelBasedScope {
	companion object {
		data object AttributeEffectBuilderSerializer :
			InlineAutoSerializer<AttributeEffectBuilder, List<AttributeEffect>>(
				serializer<List<AttributeEffect>>(),
				AttributeEffectBuilder::effects,
				::AttributeEffectBuilder,
				serialName = "AttributeEffectBuilder",
			)
	}
}

/**
 * Appends a modifier of [amount] applied to [attribute] with [operation], identified by [id].
 *
 * ```kotlin
 * attributes {
 *     attribute(AttributeModifierArgument("swift", name), Attributes.MOVEMENT_SPEED, ADD_MULTIPLIED_BASE) {
 *         amount(0.1f)
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#attributes
 */
fun AttributeEffectBuilder.attribute(
	id: AttributeModifierArgument,
	attribute: AttributeArgument,
	operation: AttributeModifierOperation,
	amount: LevelBased = Constant(0f),
	block: AttributeEffect.() -> Unit = {},
) = apply { effects += AttributeEffect(id, attribute, operation, amount).apply(block) }

/** Appends a modifier identified by `namespace:name`, applied to [attribute] with [operation]. */
fun AttributeEffectBuilder.attribute(
	name: String,
	namespace: String = "minecraft",
	attribute: AttributeArgument,
	operation: AttributeModifierOperation,
	amount: LevelBased = Constant(0f),
	block: AttributeEffect.() -> Unit = {},
) = attribute(AttributeModifierArgument(name, namespace), attribute, operation, amount, block)
