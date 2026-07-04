package io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles

import io.github.ayfri.kore.features.worldgen.floatproviders.FloatProvider
import kotlinx.serialization.Serializable

@Serializable
data class ParticleVelocity(
	var base: FloatProvider? = null,
	var movementScale: Float? = null,
)
