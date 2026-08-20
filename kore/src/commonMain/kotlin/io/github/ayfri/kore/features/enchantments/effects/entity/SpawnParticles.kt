package io.github.ayfri.kore.features.enchantments.effects.entity

import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.ParticlePosition
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.ParticlePositionType
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.ParticleVelocity
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types.ParticleType
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types.ParticleTypeScope
import io.github.ayfri.kore.features.worldgen.floatproviders.FloatProvider
import io.github.ayfri.kore.features.worldgen.floatproviders.constantFloatProvider
import kotlinx.serialization.Serializable

/**
 * Spawns one [particle] around the affected entity, placed and pushed by the horizontal and vertical settings.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#spawn_particles
 *
 * @property particle The particle spawned, along with the options its kind understands.
 * @property horizontalPosition Where the particle spawns on the X and Z axes.
 * @property verticalPosition Where the particle spawns on the Y axis.
 * @property horizontalVelocity How fast the particle flies on the X and Z axes.
 * @property verticalVelocity How fast the particle flies on the Y axis.
 */
@Serializable
data class SpawnParticles(
	var particle: ParticleType,
	var horizontalPosition: ParticlePosition,
	var verticalPosition: ParticlePosition,
	var horizontalVelocity: ParticleVelocity = ParticleVelocity(),
	var verticalVelocity: ParticleVelocity = ParticleVelocity(),
) : EntityEffect(), ParticleTypeScope

/** Sets [SpawnParticles.horizontalPosition], where the particle spawns on the X and Z axes. */
fun SpawnParticles.horizontalPosition(type: ParticlePositionType, offset: Float? = null, scale: Float? = null) {
	horizontalPosition = ParticlePosition(type, offset, scale)
}

/** Sets [SpawnParticles.verticalPosition], where the particle spawns on the Y axis. */
fun SpawnParticles.verticalPosition(type: ParticlePositionType, offset: Float? = null, scale: Float? = null) {
	verticalPosition = ParticlePosition(type, offset, scale)
}

/** Sets [SpawnParticles.horizontalVelocity], how fast the particle flies on the X and Z axes. */
fun SpawnParticles.horizontalVelocity(base: FloatProvider? = null, movementScale: Float? = null) {
	horizontalVelocity = ParticleVelocity(base, movementScale)
}

/** Sets [SpawnParticles.horizontalVelocity] to a constant [base] speed, on top of [movementScale] of the entity motion. */
fun SpawnParticles.horizontalVelocity(base: Float, movementScale: Float? = null) {
	horizontalVelocity = ParticleVelocity(constantFloatProvider(base), movementScale)
}

/** Sets [SpawnParticles.verticalVelocity], how fast the particle flies on the Y axis. */
fun SpawnParticles.verticalVelocity(base: FloatProvider? = null, movementScale: Float? = null) {
	verticalVelocity = ParticleVelocity(base, movementScale)
}

/** Sets [SpawnParticles.verticalVelocity] to a constant [base] speed, on top of [movementScale] of the entity motion. */
fun SpawnParticles.verticalVelocity(base: Float, movementScale: Float? = null) {
	verticalVelocity = ParticleVelocity(constantFloatProvider(base), movementScale)
}
