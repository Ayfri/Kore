package io.github.ayfri.kore.features.worldgen.environmentattributes.types

import io.github.ayfri.kore.data.particle.ParticleOptions
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributeModifier
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributesScope
import io.github.ayfri.kore.features.worldgen.environmentattributes.environmentAttributeValue
import io.github.ayfri.kore.generated.EnvironmentAttributes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/** A particle entry with its options and spawn probability. */
@Serializable
data class Particle(
	var particle: ParticleOptions,
	var probability: Float,
)

/** A single particle with its options, used for the dripstone particle attribute. */
@Serializable(with = ParticleOptionsValue.Companion.ParticleOptionsValueSerializer::class)
data class ParticleOptionsValue(var value: ParticleOptions) : EnvironmentAttributesType() {
	companion object {
		data object ParticleOptionsValueSerializer : InlineAutoSerializer<ParticleOptionsValue, ParticleOptions>(
			ParticleOptions.serializer(),
			ParticleOptionsValue::value,
			::ParticleOptionsValue,
			serialName = "ParticleOptionsValue",
		)
	}
}

/** A list of ambient particles that randomly spawn around the camera. */
@Serializable(with = ParticleValue.Companion.AmbiantParticlesSerializer::class)
data class ParticleValue(
	var list: List<Particle> = emptyList(),
) : EnvironmentAttributesType() {
	companion object {
		data object AmbiantParticlesSerializer : InlineAutoSerializer<ParticleValue, List<Particle>>(
			serializer<List<Particle>>(),
			ParticleValue::list,
			::ParticleValue
		)
	}
}

/**
 * Sets the ambient particles attribute, controlling particles that randomly spawn around the camera.
 *
 * ```
 * ambientParticles(EnvironmentAttributeModifier.APPEND) { particle(simpleParticle(Particles.ASH), 0.01f) }
 * ```
 * [EnvironmentAttributeModifier.APPEND] adds the particles after the ones of the lower-priority source instead of replacing them.
 */
fun EnvironmentAttributesScope.ambientParticles(mod: EnvironmentAttributeModifier.List? = null, block: ParticleValue.() -> Unit) = apply {
	this[EnvironmentAttributes.Visual.AMBIENT_PARTICLES] = environmentAttributeValue(ParticleValue().apply(block), mod)
}

/**
 * Sets the default particle dripped from Dripstone blocks when no fluid is placed above.
 *
 * ```
 * defaultDripstoneParticle(simpleParticle(Particles.DRIPPING_WATER))
 * ```
 */
fun EnvironmentAttributesScope.defaultDripstoneParticle(
	particle: ParticleOptions,
	mod: EnvironmentAttributeModifier.OVERRIDE? = null,
) = apply {
	this[EnvironmentAttributes.Visual.DEFAULT_DRIPSTONE_PARTICLE] = environmentAttributeValue(ParticleOptionsValue(particle), mod)
}

/** Adds an ambient [particle] with the chance to spawn it in an empty space. */
fun ParticleValue.particle(particle: ParticleOptions, probability: Float) = apply {
	list += Particle(particle, probability)
}
