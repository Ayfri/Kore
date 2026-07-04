package io.github.ayfri.kore.features.enchantments.effects.entity

import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.ParticlePosition
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.ParticlePositionType
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.ParticleVelocity
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types.ParticleType
import io.github.ayfri.kore.features.worldgen.floatproviders.FloatProvider
import io.github.ayfri.kore.features.worldgen.floatproviders.constantFloatProvider
import kotlinx.serialization.Serializable

@Serializable
data class SpawnParticles(
	var particle: ParticleType,
	var horizontalPosition: ParticlePosition,
	var verticalPosition: ParticlePosition,
	var horizontalVelocity: ParticleVelocity = ParticleVelocity(),
	var verticalVelocity: ParticleVelocity = ParticleVelocity(),
) : EntityEffect()

fun SpawnParticles.horizontalPosition(type: ParticlePositionType, offset: Float? = null, scale: Float? = null) {
	horizontalPosition = ParticlePosition(type, offset, scale)
}

fun SpawnParticles.verticalPosition(type: ParticlePositionType, offset: Float? = null, scale: Float? = null) {
	verticalPosition = ParticlePosition(type, offset, scale)
}

fun SpawnParticles.horizontalVelocity(base: FloatProvider? = null, movementScale: Float? = null) {
	horizontalVelocity = ParticleVelocity(base, movementScale)
}

fun SpawnParticles.horizontalVelocity(base: Float, movementScale: Float? = null) {
	horizontalVelocity = ParticleVelocity(constantFloatProvider(base), movementScale)
}

fun SpawnParticles.verticalVelocity(base: FloatProvider? = null, movementScale: Float? = null) {
	verticalVelocity = ParticleVelocity(base, movementScale)
}

fun SpawnParticles.verticalVelocity(base: Float, movementScale: Float? = null) {
	verticalVelocity = ParticleVelocity(constantFloatProvider(base), movementScale)
}
