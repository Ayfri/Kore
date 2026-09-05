package io.github.ayfri.kore.features.predicates.types

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/**
 * Which entity of the loot context a condition or a number provider reads.
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@Serializable(with = EntityTarget.Companion.EntityTargetSerializer::class)
enum class EntityTarget {
	/** The entity that dealt the killing blow. */
	ATTACKER,

	/** The player credited with the kill, if any. */
	ATTACKING_PLAYER,

	/** The direct source of the killing blow, e.g. the arrow rather than the skeleton. */
	DIRECT_ATTACKER,

	/** The entity being interacted with. */
	INTERACTING_ENTITY,

	/** The entity being targeted by the context entity's AI. */
	TARGET_ENTITY,

	/** The entity the loot context is built around. */
	THIS;

	companion object {
		data object EntityTargetSerializer : LowercaseSerializer<EntityTarget>(entries)
	}
}
