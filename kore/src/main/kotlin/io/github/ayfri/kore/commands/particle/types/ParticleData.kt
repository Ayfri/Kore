package io.github.ayfri.kore.commands.particle.types

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ParticleData.Companion.ParticleDataSerializer::class)
sealed class ParticleData {
	companion object {
		data object ParticleDataSerializer : NamespacedPolymorphicSerializer<ParticleData>(ParticleData::class, skipOutputName = true)
	}
}
