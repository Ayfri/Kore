package io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types

import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import kotlinx.serialization.Serializable

@Serializable
data class DragonBreathParticleType(
	override var type: ParticleTypeArgument,
	var power: Float? = null,
) : ParticleType()

fun dragonBreathParticleType(type: ParticleTypeArgument, power: Float? = null) = DragonBreathParticleType(type, power)
