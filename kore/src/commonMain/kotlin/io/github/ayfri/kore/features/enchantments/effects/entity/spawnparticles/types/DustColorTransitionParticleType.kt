package io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.ColorAsDoubleArraySerializer
import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The `dust_color_transition` particle, fading from [fromColor] to [toColor] as it lives.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Particle#dust_color_transition
 *
 * @property type The id of the particle.
 * @property fromColor The tint the particle starts at.
 * @property toColor The tint the particle ends at.
 * @property scale The size factor of the particle.
 */
@Serializable
data class DustColorTransitionParticleType(
	override var type: ParticleTypeArgument,
	var fromColor: @Serializable(ColorAsDoubleArraySerializer::class) Color,
	@SerialName("to_color")
	var toColor: @Serializable(ColorAsDoubleArraySerializer::class) Color,
	var scale: Double,
) : ParticleType()

/** Creates the options of the `dust_color_transition` particle. */
fun ParticleTypeScope.dustColorTransitionParticleType(
	type: ParticleTypeArgument,
	fromColor: Color,
	toColor: Color,
	scale: Double,
) = DustColorTransitionParticleType(type, fromColor, toColor, scale)
