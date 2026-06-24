package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:enchantment_glint_override` item component, which forces the enchantment glint on or off regardless of enchantments.
 *
 * Serializes as the boolean value directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#enchantment_glint_override
 */
@Serializable(with = EnchantmentGlintOverrideComponent.Companion.EnchantmentGlintOverrideComponentSerializer::class)
data class EnchantmentGlintOverrideComponent(var glint: Boolean) : Component() {
	companion object {
		data object EnchantmentGlintOverrideComponentSerializer : InlineAutoSerializer<EnchantmentGlintOverrideComponent>(
			EnchantmentGlintOverrideComponent::class
		)
	}
}

/** Forces the enchantment glint on or off regardless of enchantments. */
fun ComponentsScope.enchantmentGlintOverride(glint: Boolean) = apply {
	this[ItemComponentTypes.ENCHANTMENT_GLINT_OVERRIDE] = EnchantmentGlintOverrideComponent(glint)
}
