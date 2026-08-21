package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.generated.Tags
import kotlinx.serialization.Serializable

/**
 * Requires the damage type to be in (or out of) a damage type tag.
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@Serializable
data class DamageTagPredicate(
	var id: Tags.DamageType,
	/** Whether the damage type is expected to have or not have the tag. */
	var expected: Boolean,
)

/**
 * Matches the source of some damage, as the `type` key of a [DamagePredicate] or the `predicate` key of the
 * `damage_source_properties` condition.
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@Serializable
data class DamageSourcePredicate(
	/** Direct entity responsible for the damage, e.g. the arrow or the TNT. */
	var directEntity: EntityPredicate? = null,
	/** Damage is direct when its direct and source entities are the same. */
	var isDirect: Boolean? = null,
	/** Entity responsible for the damage, e.g. the skeleton that shot the arrow. */
	var sourceEntity: EntityPredicate? = null,
	/** Damage type tags the damage type must be in. */
	var tags: List<DamageTagPredicate>? = null,
)

/** Creates a [DamageSourcePredicate]. */
fun damageSourcePredicate(init: DamageSourcePredicate.() -> Unit = {}) = DamageSourcePredicate().apply(init)

/** Matches the direct entity responsible for the damage. */
fun DamageSourcePredicate.directEntity(init: EntityPredicate.() -> Unit) {
	directEntity = EntityPredicate().apply(init)
}

/** Matches the entity responsible for the damage. */
fun DamageSourcePredicate.sourceEntity(init: EntityPredicate.() -> Unit) {
	sourceEntity = EntityPredicate().apply(init)
}

/** Requires the damage type to be in [id], or out of it when [expected] is `false`. */
fun DamageSourcePredicate.tag(id: Tags.DamageType, expected: Boolean = true) {
	tags = (tags ?: emptyList()) + DamageTagPredicate(id, expected)
}
