package io.github.ayfri.kore.features.enchantment.effects.entity.spawnparticles.types

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.ColorAsDoubleArraySerializer
import io.github.ayfri.kore.arguments.types.resources.ParticleArgument
import kotlinx.serialization.Serializable

@Serializable
data class EntityEffectParticleType(
	override var type: ParticleArgument,
	var color: @Serializable(ColorAsDoubleArraySerializer::class) Color,
) : ParticleType()

fun entityEffectParticleType(type: ParticleArgument, color: Color) = EntityEffectParticleType(type, color)
