package io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types

import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import kotlinx.serialization.Serializable

/**
 * A particle that has no options of its own.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Particle#simple
 *
 * @property type The id of the particle.
 */
@Serializable
data class SimpleParticleType(
	override var type: ParticleTypeArgument,
) : ParticleType()

/** Creates the options of a particle that has none of its own. */
fun ParticleTypeScope.particleType(type: ParticleTypeArgument) = SimpleParticleType(type)
