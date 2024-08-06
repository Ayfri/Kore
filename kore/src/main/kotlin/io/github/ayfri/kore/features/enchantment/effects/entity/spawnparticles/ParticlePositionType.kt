package io.github.ayfri.kore.features.enchantment.effects.entity.spawnparticles

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ParticlePositionType.Companion.ParticlePositionTypeSerializer::class)
enum class ParticlePositionType {
	ENTITY_POSITION,
	IN_BOUNDING_BOX;

	companion object {
		data object ParticlePositionTypeSerializer : LowercaseSerializer<ParticlePositionType>(entries)
	}
}
