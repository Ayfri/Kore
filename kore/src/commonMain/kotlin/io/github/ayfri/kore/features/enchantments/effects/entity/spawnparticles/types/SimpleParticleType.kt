package io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types

import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import kotlinx.serialization.Serializable

@Serializable
data class SimpleParticleType(
	override var type: ParticleTypeArgument,
) : ParticleType()

fun particleType(type: ParticleTypeArgument) = SimpleParticleType(type)
