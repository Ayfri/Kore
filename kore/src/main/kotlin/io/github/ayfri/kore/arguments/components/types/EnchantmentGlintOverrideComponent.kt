package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

@Serializable(with = EnchantmentGlintOverrideComponent.Companion.EnchantmentGlintOverrideComponentSerializer::class)
data class EnchantmentGlintOverrideComponent(var glint: Boolean) : Component() {
	companion object {
		object EnchantmentGlintOverrideComponentSerializer : InlineSerializer<EnchantmentGlintOverrideComponent, Boolean>(
			Boolean.serializer(),
			EnchantmentGlintOverrideComponent::glint
		)
	}
}

fun ComponentsScope.enchantmentGlintOverride(glint: Boolean) = apply {
	this[ItemComponentTypes.ENCHANTMENT_GLINT_OVERRIDE] = EnchantmentGlintOverrideComponent(glint)
}
