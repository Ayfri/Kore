package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.generated.arguments.DamageTypeOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Requires the damage type to match (or not match) one of the given damage types or damage type tags.
 *
 * [id] is a damage type, a `#tag` or a list of them, a single entry being written bare.
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@Serializable
data class DamageTagPredicate(
	/** Whether the damage type is expected to match or not to match [id]. */
	var expected: Boolean,
	var id: InlinableList<DamageTypeOrTagArgument>,
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

/**
 * Requires the damage type to be one of [ids] (damage types or damage type tags), or none of them when [expected] is `false`.
 *
 * ```
 * tag(Tags.DamageType.IS_FIRE)                         // "id": "#minecraft:is_fire"
 * tag(DamageTypes.LAVA, DamageTypes.IN_FIRE)           // "id": ["minecraft:lava", "minecraft:in_fire"]
 * ```
 */
fun DamageSourcePredicate.tag(vararg ids: DamageTypeOrTagArgument, expected: Boolean = true) {
	tags = (tags ?: emptyList()) + DamageTagPredicate(expected, ids.toList())
}
