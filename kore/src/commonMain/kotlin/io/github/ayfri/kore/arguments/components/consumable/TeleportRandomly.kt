package io.github.ayfri.kore.arguments.components.consumable

import kotlinx.serialization.Serializable

/**
 * Teleports the consumer to a random position within [diameter] blocks.
 *
 * @property directionalParticles Whether the teleport particles follow the direction of the move, `true` when `null`.
 */
@Serializable
data class TeleportRandomly(
	var diameter: Float,
	var directionalParticles: Boolean? = null,
) : ConsumeEffect()

fun ConsumeEffects.teleportRandomly(diameter: Float, directionalParticles: Boolean? = null) =
	apply { effects += TeleportRandomly(diameter, directionalParticles) }
