package io.github.ayfri.kore.features.enchantment.effects.entity.spawnparticles.types

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.ColorAsDoubleArraySerializer
import io.github.ayfri.kore.arguments.types.resources.ParticleArgument
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DustColorTransitionParticleType(
	override var type: ParticleArgument,
	var fromColor: @Serializable(ColorAsDoubleArraySerializer::class) Color,
	@SerialName("to_color")
	var toColor: @Serializable(ColorAsDoubleArraySerializer::class) Color,
	var scale: Double,
) : ParticleType()

fun dustColorTransitionParticleType(type: ParticleArgument, fromColor: Color, toColor: Color, scale: Double) =
	DustColorTransitionParticleType(type, fromColor, toColor, scale)
