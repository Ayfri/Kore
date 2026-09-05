package io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles

import io.github.ayfri.kore.features.worldgen.floatproviders.FloatProvider
import kotlinx.serialization.Serializable

/**
 * How fast a `spawn_particles` particle flies on one axis, adding [base] to [movementScale] times the motion of the
 * entity the effect runs on.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#spawn_particles
 *
 * @property base The speed added whatever the entity does, `0` when `null`.
 * @property movementScale The share of the entity motion carried over, `0` when `null`.
 */
@Serializable
data class ParticleVelocity(
	var base: FloatProvider? = null,
	var movementScale: Float? = null,
)
