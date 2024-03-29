package io.github.ayfri.kore.arguments.components

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
	components["enchantment_glint_override"] = EnchantmentGlintOverrideComponent(glint)
}
