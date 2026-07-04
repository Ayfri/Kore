package io.github.ayfri.kore.commands.particle.types

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = ParticleData.Companion.ParticleDataSerializer::class)
sealed class ParticleData {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object ParticleDataSerializer :
			NamespacedPolymorphicSerializer<ParticleData>(particleDataSealedSerializer(), skipOutputName = true)
	}
}
