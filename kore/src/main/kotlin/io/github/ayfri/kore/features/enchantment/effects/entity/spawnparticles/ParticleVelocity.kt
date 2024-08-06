package io.github.ayfri.kore.features.enchantment.effects.entity.spawnparticles

import io.github.ayfri.kore.features.worldgen.floatproviders.FloatProvider
import kotlinx.serialization.Serializable

@Serializable
data class ParticleVelocity(
	var base: FloatProvider? = null,
	var movementScale: Float? = null,
)
