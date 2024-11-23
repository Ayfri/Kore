package io.github.ayfri.kore.arguments.components.consumable

import kotlinx.serialization.Serializable

@Serializable
data class ConsumeEffects(
	var effects: List<ConsumeEffect> = emptyList(),
)
