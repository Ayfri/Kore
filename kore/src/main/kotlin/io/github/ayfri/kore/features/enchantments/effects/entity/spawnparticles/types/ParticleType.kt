package io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types

import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ParticleType.Companion.ParticleTypeSerializer::class)
sealed class ParticleType {
	abstract var type: ParticleTypeArgument

	companion object {
		data object ParticleTypeSerializer : NamespacedPolymorphicSerializer<ParticleType>(
			ParticleType::class,
			outputName = "__type__",
			skipOutputName = true
		)
	}
}
