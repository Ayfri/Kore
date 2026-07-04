package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.features.predicates.sub.item.ItemStackSubPredicates
import kotlinx.serialization.Serializable

@Serializable
data class Damage(
	var durability: IntRangeOrIntJson? = null,
	var damage: IntRangeOrIntJson? = null,
) : ComponentMatcher()

fun ItemStackSubPredicates.damage(init: Damage.() -> Unit) = apply { matchers += Damage().apply(init) }

fun Damage.durability(value: Int) {
	durability = rangeOrInt(value)
}

fun Damage.damage(value: Int) {
	damage = rangeOrInt(value)
}
