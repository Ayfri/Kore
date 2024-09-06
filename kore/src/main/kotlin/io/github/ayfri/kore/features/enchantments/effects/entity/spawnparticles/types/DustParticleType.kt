package io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.ColorAsDoubleArraySerializer
import io.github.ayfri.kore.arguments.types.resources.ParticleArgument
import kotlinx.serialization.Serializable

@Serializable
data class DustParticleType(
	override var type: ParticleArgument,
	var color: @Serializable(ColorAsDoubleArraySerializer::class) Color,
	var scale: Double,
) : ParticleType()

fun dustParticleType(type: ParticleArgument, color: Color, scale: Double) = DustParticleType(type, color, scale)
