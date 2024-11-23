package io.github.ayfri.kore.arguments.components.consumable

import io.github.ayfri.kore.arguments.components.types.Effect
import kotlinx.serialization.Serializable

@Serializable
data class RemoveEffects(
	var effects: List<Effect> = emptyList(),
) : ConsumeEffect()

fun ConsumeEffects.removeEffects(vararg effects: Effect) = apply { this.effects += RemoveEffects(effects.toList()) }
