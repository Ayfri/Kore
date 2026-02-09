package io.github.ayfri.kore.features.worldgen.environmentattributes.types

import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributeModifier
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributesScope
import io.github.ayfri.kore.features.worldgen.environmentattributes.environmentAttributeValue
import io.github.ayfri.kore.generated.EnvironmentAttributes
import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/** A particle entry with its options and spawn probability. */
@Serializable
data class Particle(
	var options: ParticleOptions,
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
		data object AmbiantParticlesSerializer : InlineAutoSerializer<ParticleValue>(ParticleValue::class)
	}
}

/** Sets the ambient particles attribute, controlling particles that randomly spawn around the camera. */
fun EnvironmentAttributesScope.ambientParticles(
	list: List<Particle>,
	mod: EnvironmentAttributeModifier.OVERRIDE? = null,
	block: ParticleValue.() -> Unit = {},
) = apply {
	this[EnvironmentAttributes.Visual.AMBIENT_PARTICLES] = environmentAttributeValue(ParticleValue(list).apply(block), mod)
}

fun EnvironmentAttributesScope.ambientParticles(
	vararg list: Particle,
	mod: EnvironmentAttributeModifier.OVERRIDE? = null,
	block: ParticleValue.() -> Unit = {},
) = apply {
	this[EnvironmentAttributes.Visual.AMBIENT_PARTICLES] =
		environmentAttributeValue(ParticleValue(list.toList()).apply(block), mod)
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

fun ParticleValue.entry(particle: ParticleTypeArgument, probability: Float) = apply {
	list += Particle(ParticleOptions(particle), probability)
}
