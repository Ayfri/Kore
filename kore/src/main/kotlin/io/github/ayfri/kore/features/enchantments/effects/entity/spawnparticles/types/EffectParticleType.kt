package io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.ColorAsDoubleArraySerializer
import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import kotlinx.serialization.Serializable

@Serializable
data class EffectParticleType(
	override var type: ParticleTypeArgument,
	var color: @Serializable(ColorAsDoubleArraySerializer::class) Color? = null,
	var power: Float? = null,
) : ParticleType()

fun effectParticleType(type: ParticleTypeArgument, color: Color? = null, power: Float? = null) =
	EffectParticleType(type, color, power)
