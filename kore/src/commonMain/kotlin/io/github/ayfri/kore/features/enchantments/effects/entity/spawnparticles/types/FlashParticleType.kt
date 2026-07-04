package io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types

import io.github.ayfri.kore.arguments.colors.ARGB
import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import kotlinx.serialization.Serializable

@Serializable
data class FlashParticleType(
	override var type: ParticleTypeArgument,
	var color: ARGB,
) : ParticleType()

fun flashParticleType(type: ParticleTypeArgument, color: ARGB) = FlashParticleType(type, color)
