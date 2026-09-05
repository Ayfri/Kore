package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.arguments.numbers.ranges.serializers.FloatRangeOrFloatJson
import kotlinx.serialization.Serializable

/**
 * Matches the damage an entity is taking, including how much of it got through and where it came from.
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@Serializable
data class DamagePredicate(
	/** Whether the damage was successfully blocked. */
	var blocked: Boolean? = null,
	/** Amount of incoming damage before damage reduction. */
	var dealt: FloatRangeOrFloatJson? = null,
	/** Entity responsible for the damage, e.g. the skeleton that shot the arrow. */
	var sourceEntity: EntityPredicate? = null,
	/** Amount of incoming damage after damage reduction. */
	var taken: FloatRangeOrFloatJson? = null,
	/** The damage source itself. */
	var type: DamageSourcePredicate? = null,
)

/** Creates a [DamagePredicate]. */
fun damagePredicate(init: DamagePredicate.() -> Unit = {}) = DamagePredicate().apply(init)

/** Matches the entity responsible for the damage. */
fun DamagePredicate.sourceEntity(init: EntityPredicate.() -> Unit) {
	sourceEntity = EntityPredicate().apply(init)
}

/** Matches the damage source. */
fun DamagePredicate.type(init: DamageSourcePredicate.() -> Unit) {
	type = DamageSourcePredicate().apply(init)
}
