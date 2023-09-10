package io.github.ayfri.kore.features.predicates.types

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = EntityType.Companion.EntitySerializer::class)
enum class EntityType {
	DIRECT_KILLER,
	KILLER,
	KILLER_PLAYER,
	THIS;

	companion object {
		data object EntitySerializer : LowercaseSerializer<EntityType>(entries)
	}
}
