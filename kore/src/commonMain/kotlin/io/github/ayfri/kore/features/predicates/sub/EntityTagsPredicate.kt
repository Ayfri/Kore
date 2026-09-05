package io.github.ayfri.kore.features.predicates.sub

import kotlinx.serialization.Serializable

/**
 * Matches an entity against scoreboard-style tags set through `/tag`, keyed under `minecraft:entity_tags`.
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@Serializable
data class EntityTagsPredicate(
	/** The entity must have all of these tags. */
	var allOf: List<String>? = null,
	/** The entity must have at least one of these tags. */
	var anyOf: List<String>? = null,
	/** The entity must have none of these tags. */
	var noneOf: List<String>? = null,
)

/** Creates an [EntityTagsPredicate]. */
fun entityTagsPredicate(init: EntityTagsPredicate.() -> Unit = {}) = EntityTagsPredicate().apply(init)

/** Requires the entity to have all of [tags]. */
fun EntityTagsPredicate.allOf(vararg tags: String) {
	allOf = tags.toList()
}

/** Requires the entity to have at least one of [tags]. */
fun EntityTagsPredicate.anyOf(vararg tags: String) {
	anyOf = tags.toList()
}

/** Requires the entity to have none of [tags]. */
fun EntityTagsPredicate.noneOf(vararg tags: String) {
	noneOf = tags.toList()
}
