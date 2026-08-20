package io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.ColorAsDoubleArraySerializer
import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import kotlinx.serialization.Serializable

/**
 * The `dust` particle, tinted [color] and drawn at [scale] times its usual size.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Particle#dust
 *
 * @property type The id of the particle.
 * @property color The tint of the particle.
 * @property scale The size factor of the particle.
 */
@Serializable
data class DustParticleType(
	override var type: ParticleTypeArgument,
	var color: @Serializable(ColorAsDoubleArraySerializer::class) Color,
	var scale: Double,
) : ParticleType()

/** Creates the options of the `dust` particle. */
fun ParticleTypeScope.dustParticleType(type: ParticleTypeArgument, color: Color, scale: Double) = DustParticleType(type, color, scale)
