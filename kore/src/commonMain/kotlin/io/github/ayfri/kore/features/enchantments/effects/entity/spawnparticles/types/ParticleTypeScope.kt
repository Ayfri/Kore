package io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types

/**
 * Receiver of the [ParticleType] builders, implemented by everything that accepts particle options.
 *
 * Scoping them this way keeps `particleType`, `dustParticleType` and friends out of the global completion list:
 * they only resolve inside a block that actually takes a particle, such as `spawnParticles(...)` or `explode { }`.
 *
 * Outside such a block, [ParticleType.Companion] is a scope of its own, so `ParticleType.particleType(...)` builds
 * options anywhere.
 */
interface ParticleTypeScope
