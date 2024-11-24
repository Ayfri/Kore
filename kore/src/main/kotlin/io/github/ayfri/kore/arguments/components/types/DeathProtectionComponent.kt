package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ComponentTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeathProtectionComponent(
	@SerialName("death_effects")
	var deathEffects: List<Effect>? = null,
) : Component()

fun ComponentsScope.deathProtection(deathEffects: List<Effect>? = null) = apply {
	this[ComponentTypes.DEATH_PROTECTION] = DeathProtectionComponent(deathEffects)
}

fun ComponentsScope.deathProtection(vararg effects: Effect) = apply {
	this[ComponentTypes.DEATH_PROTECTION] = DeathProtectionComponent(effects.toList())
}

fun ComponentsScope.deathProtection(block: DeathProtectionComponent.() -> Unit) = apply {
	this[ComponentTypes.DEATH_PROTECTION] = DeathProtectionComponent().apply(block)
}

fun DeathProtectionComponent.effects(vararg effects: Effect) {
	deathEffects = effects.toList()
}
