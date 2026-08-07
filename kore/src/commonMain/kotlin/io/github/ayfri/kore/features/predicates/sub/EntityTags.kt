package io.github.ayfri.kore.features.predicates.sub

import kotlinx.serialization.Serializable

/**
 * Matches an entity against scoreboard-style tags set through `/tag`.
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@Serializable
data class EntityTagsPredicate(
	var allOf: List<String>? = null,
	var anyOf: List<String>? = null,
	var noneOf: List<String>? = null,
)

fun entityTagsPredicate(init: EntityTagsPredicate.() -> Unit = {}) = EntityTagsPredicate().apply(init)

fun EntityTagsPredicate.allOf(vararg tags: String) {
	allOf = tags.toList()
}

fun EntityTagsPredicate.anyOf(vararg tags: String) {
	anyOf = tags.toList()
}

fun EntityTagsPredicate.noneOf(vararg tags: String) {
	noneOf = tags.toList()
}
