package io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/**
 * What a `spawn_particles` offset is measured against.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#spawn_particles
 */
@Serializable(with = ParticlePositionType.Companion.ParticlePositionTypeSerializer::class)
enum class ParticlePositionType {
	/** The offset is measured from the position of the entity, at its feet. */
	ENTITY_POSITION,

	/** The offset is measured from the hitbox of the entity, scaled by its size. */
	IN_BOUNDING_BOX;

	companion object {
		data object ParticlePositionTypeSerializer : LowercaseSerializer<ParticlePositionType>(entries)
	}
}
