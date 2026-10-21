package io.github.ayfri.kore.features.worldgen.environmentattributes.types

import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributeModifier
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributesScope
import io.github.ayfri.kore.features.worldgen.environmentattributes.environmentAttributeValue
import io.github.ayfri.kore.generated.EnvironmentAttributes
import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/** A particle entry with its options and spawn probability. */
@Serializable
data class Particle(
	var particle: ParticleOptions,
	var probability: Float,
)

/** Particle type definition, used for dripstone and ambient particle attributes. */
@Serializable
data class ParticleOptions(
	var type: ParticleTypeArgument,
) : EnvironmentAttributesType()


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
 * ambientParticles(EnvironmentAttributeModifier.APPEND) { particle(Particles.ASH, 0.01f) }
 * ```
 * [EnvironmentAttributeModifier.APPEND] adds the particles after the ones of the lower-priority source instead of replacing them.
 */
fun EnvironmentAttributesScope.ambientParticles(mod: EnvironmentAttributeModifier.List? = null, block: ParticleValue.() -> Unit) = apply {
	this[EnvironmentAttributes.Visual.AMBIENT_PARTICLES] = environmentAttributeValue(ParticleValue().apply(block), mod)
}

/** Sets the default particle dripped from Dripstone blocks when no fluid is placed above. */
fun EnvironmentAttributesScope.defaultDripstoneParticle(
	type: ParticleTypeArgument,
	mod: EnvironmentAttributeModifier.OVERRIDE? = null,
	block: ParticleOptions.() -> Unit = {},
) = apply {
	this[EnvironmentAttributes.Visual.DEFAULT_DRIPSTONE_PARTICLE] =
		environmentAttributeValue(ParticleOptions(type).apply(block), mod)
}

/** Adds an ambient particle with the chance to spawn it in an empty space. */
fun ParticleValue.particle(type: ParticleTypeArgument, probability: Float) = apply {
	list += Particle(ParticleOptions(type), probability)
}
