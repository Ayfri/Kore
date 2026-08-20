package io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types

import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import kotlinx.serialization.Serializable

/**
 * The `dragon_breath` particle, lingering harder the higher [power] is.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Particle#dragon_breath
 *
 * @property type The id of the particle.
 * @property power How long the cloud lingers, the vanilla default when `null`.
 */
@Serializable
data class DragonBreathParticleType(
	override var type: ParticleTypeArgument,
	var power: Float? = null,
) : ParticleType()

/** Creates the options of the `dragon_breath` particle. */
fun ParticleTypeScope.dragonBreathParticleType(type: ParticleTypeArgument, power: Float? = null) = DragonBreathParticleType(type, power)
