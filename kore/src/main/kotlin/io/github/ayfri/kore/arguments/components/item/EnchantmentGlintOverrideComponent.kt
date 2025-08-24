package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = EnchantmentGlintOverrideComponent.Companion.EnchantmentGlintOverrideComponentSerializer::class)
data class EnchantmentGlintOverrideComponent(var glint: Boolean) : Component() {
	companion object {
		data object EnchantmentGlintOverrideComponentSerializer : InlineAutoSerializer<EnchantmentGlintOverrideComponent>(
			EnchantmentGlintOverrideComponent::class
		)
	}
}

fun ComponentsScope.enchantmentGlintOverride(glint: Boolean) = apply {
	this[ItemComponentTypes.ENCHANTMENT_GLINT_OVERRIDE] = EnchantmentGlintOverrideComponent(glint)
}
