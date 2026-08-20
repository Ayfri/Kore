package io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types

import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * The particle spawned by a `spawn_particles` enchantment effect or thrown by an `explode` one, along with the
 * options its kind understands.
 *
 * Every builder is an extension on [ParticleTypeScope], so they only resolve where a particle is actually accepted.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Particle
 */
@GeneratedSealedSerializer
@Serializable(with = ParticleType.Companion.ParticleTypeSerializer::class)
sealed class ParticleType {
	/** The id of the particle, deciding which options are read. */
	abstract var type: ParticleTypeArgument

	companion object : ParticleTypeScope {
		@OptIn(InternalSerializationApi::class)
		data object ParticleTypeSerializer : NamespacedPolymorphicSerializer<ParticleType>(
			particleTypeSealedSerializer(),
			outputName = "__type__",
			skipOutputName = true
		)
	}
}
