package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

@Serializable
data class DamageComponentMatcher(
	var durability: IntRangeOrIntJson? = null,
	var damage: IntRangeOrIntJson? = null,
) : ComponentMatcher()

fun DataComponentPredicate.damage(init: DamageComponentMatcher.() -> Unit) = apply { matchers += DamageComponentMatcher().apply(init) }

fun DamageComponentMatcher.durability(value: Int) {
	durability = rangeOrInt(value)
}

fun DamageComponentMatcher.damage(value: Int) {
	damage = rangeOrInt(value)
}
