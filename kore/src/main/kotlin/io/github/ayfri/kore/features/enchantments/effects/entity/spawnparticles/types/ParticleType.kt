package io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types

import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = ParticleType.Companion.ParticleTypeSerializer::class)
sealed class ParticleType {
	abstract var type: ParticleTypeArgument

	companion object {
		@OptIn(InternalSerializationApi::class)
		data object ParticleTypeSerializer : NamespacedPolymorphicSerializer<ParticleType>(
			particleTypeSealedSerializer(),
			outputName = "__type__",
			skipOutputName = true
		)
	}
}
