package io.github.ayfri.kore.arguments.components.consumable

import kotlinx.serialization.Serializable

@Serializable
data class TeleportRandomly(
	var diameter: Float,
) : ConsumeEffect()

fun ConsumeEffects.teleportRandomly(diameter: Float) = apply { effects += TeleportRandomly(diameter) }
