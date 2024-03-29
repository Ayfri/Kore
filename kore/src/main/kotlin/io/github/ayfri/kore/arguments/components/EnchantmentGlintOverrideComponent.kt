package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.generated.ComponentTypes
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

fun Components.enchantmentGlintOverride(glint: Boolean) = apply {
	this[ComponentTypes.ENCHANTMENT_GLINT_OVERRIDE] = EnchantmentGlintOverrideComponent(glint)
}
