package io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types

import io.github.ayfri.kore.arguments.colors.ARGB
import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import kotlinx.serialization.Serializable

/**
 * The `flash` particle, tinted [color].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Particle#flash
 *
 * @property type The id of the particle.
 * @property color The ARGB tint of the flash.
 */
@Serializable
data class FlashParticleType(
	override var type: ParticleTypeArgument,
	var color: ARGB,
) : ParticleType()

/** Creates the options of the `flash` particle. */
fun ParticleTypeScope.flashParticleType(type: ParticleTypeArgument, color: ARGB) = FlashParticleType(type, color)
