package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.arguments.components.CollectionMatcher
import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.features.predicates.sub.item.ItemStackSubPredicates
import kotlinx.serialization.Serializable

@Serializable
data class FireworksComponentMatcher(
	var explosions: CollectionMatcher<FireworkExplosionComponentMatcher>? = null,
	var flightDuration: IntRangeOrIntJson? = null,
) : ComponentMatcher()

fun ItemStackSubPredicates.fireworks(init: FireworksComponentMatcher.() -> Unit) =
	apply { matchers += FireworksComponentMatcher().apply(init) }

fun FireworksComponentMatcher.explosions(block: CollectionMatcher<FireworkExplosionComponentMatcher>.() -> Unit) {
	explosions = CollectionMatcher<FireworkExplosionComponentMatcher>().apply(block)
}
