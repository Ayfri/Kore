package io.github.ayfri.kore.features.enchantment.effects.entity.spawnparticles.types

import io.github.ayfri.kore.arguments.types.resources.ParticleArgument
import kotlinx.serialization.Serializable

@Serializable
data class SimpleParticleType(
	override var type: ParticleArgument,
) : ParticleType()

fun particleType(type: ParticleArgument) = SimpleParticleType(type)
