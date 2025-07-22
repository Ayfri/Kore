package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeathProtectionComponent(
	@SerialName("death_effects")
	var deathEffects: List<Effect>? = null,
) : Component()

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
