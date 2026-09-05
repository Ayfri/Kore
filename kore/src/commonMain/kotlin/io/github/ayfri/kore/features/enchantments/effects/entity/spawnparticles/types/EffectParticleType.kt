package io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.ColorAsDoubleArraySerializer
import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import kotlinx.serialization.Serializable

/**
 * The `entity_effect` particle, tinted [color].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Particle#entity_effect
 *
 * @property type The id of the particle.
 * @property color The tint of the particle, rolled by the game when `null`.
 * @property power How strongly the particle is drawn, the vanilla default when `null`.
 */
@Serializable
data class EffectParticleType(
	override var type: ParticleTypeArgument,
	var color: @Serializable(ColorAsDoubleArraySerializer::class) Color? = null,
	var power: Float? = null,
) : ParticleType()

/** Creates the options of the `entity_effect` particle. */
fun ParticleTypeScope.effectParticleType(type: ParticleTypeArgument, color: Color? = null, power: Float? = null) =
	EffectParticleType(type, color, power)
