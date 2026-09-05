package io.github.ayfri.kore.arguments.components.consumable

import io.github.ayfri.kore.arguments.components.item.Effect
import kotlinx.serialization.Serializable

@Serializable
data class ApplyEffects(
	var effects: List<Effect>,
	var probability: Float,
) : ConsumeEffect()

fun ConsumeEffects.applyEffects(probability: Float, vararg effects: Effect) =
	apply { this.effects += ApplyEffects(effects.toList(), probability) }
