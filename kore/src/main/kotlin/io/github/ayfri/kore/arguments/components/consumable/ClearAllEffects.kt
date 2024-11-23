package io.github.ayfri.kore.arguments.components.consumable

import kotlinx.serialization.Serializable

@Serializable
data object ClearAllEffects : ConsumeEffect()

fun ConsumeEffects.clearAllEffects() = apply { effects += ClearAllEffects }
