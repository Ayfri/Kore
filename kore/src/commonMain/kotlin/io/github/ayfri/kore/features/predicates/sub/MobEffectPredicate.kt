package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

/**
 * Matches one active status effect, as a value of the `minecraft:effects` sub-predicate map.
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@Serializable
data class MobEffectPredicate(
	var ambient: Boolean? = null,
	var amplifier: IntRangeOrIntJson? = null,
	var duration: IntRangeOrIntJson? = null,
	var visible: Boolean? = null,
)

/** Creates a [MobEffectPredicate]. */
fun mobEffectPredicate(init: MobEffectPredicate.() -> Unit = {}) = MobEffectPredicate().apply(init)
