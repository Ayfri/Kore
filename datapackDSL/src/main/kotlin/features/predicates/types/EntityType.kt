package features.predicates.types

import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

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
