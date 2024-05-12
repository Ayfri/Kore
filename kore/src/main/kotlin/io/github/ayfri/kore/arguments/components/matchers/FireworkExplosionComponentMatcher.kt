package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.arguments.components.types.FireworkExplosionShape
import io.github.ayfri.kore.features.predicates.sub.item.ItemStackSubPredicates
import kotlinx.serialization.Serializable

@Serializable
data class FireworkExplosionComponentMatcher(
	var shape: FireworkExplosionShape? = null,
	var hasTrail: Boolean? = null,
	var hasTwinkle: Boolean? = null,
) : ComponentMatcher()

fun ItemStackSubPredicates.fireworkExplosion(init: FireworkExplosionComponentMatcher.() -> Unit) =
	apply { matchers += FireworkExplosionComponentMatcher().apply(init) }
