package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:death_protection` item component, which prevents death and applies effects when the holder would die (like a totem).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#death_protection
 */
@Serializable
data class DeathProtectionComponent(
	@SerialName("death_effects")
	var deathEffects: List<Effect>? = null,
) : Component()

/** Prevents death and applies effects when the holder would die (like a totem). */
fun ComponentsScope.deathProtection(deathEffects: List<Effect>? = null) = apply {
	this[ItemComponentTypes.DEATH_PROTECTION] = DeathProtectionComponent(deathEffects)
}

fun ComponentsScope.deathProtection(vararg effects: Effect) = apply {
	this[ItemComponentTypes.DEATH_PROTECTION] = DeathProtectionComponent(effects.toList())
}

fun ComponentsScope.deathProtection(block: DeathProtectionComponent.() -> Unit) = apply {
	this[ItemComponentTypes.DEATH_PROTECTION] = DeathProtectionComponent().apply(block)
}

fun DeathProtectionComponent.effects(vararg effects: Effect) {
	deathEffects = effects.toList()
}
