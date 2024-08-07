package io.github.ayfri.kore.features.predicates.types

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = EntityType.Companion.EntitySerializer::class)
enum class EntityType {
	ATTACKER,
	ATTACKING_PLAYER,
	DIRECT_ATTACKER,
	THIS;

	companion object {
		data object EntitySerializer : LowercaseSerializer<EntityType>(entries)
	}
}
