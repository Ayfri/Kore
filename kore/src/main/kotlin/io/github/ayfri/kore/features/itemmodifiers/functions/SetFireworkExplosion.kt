package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.colors.RGB
import io.github.ayfri.kore.arguments.components.FireworkExplosionShape
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class SetFireworkExplosion(
	override var conditions: PredicateAsList? = null,
	var shape: FireworkExplosionShape,
	var colors: List<@Serializable(RGB.Companion.ColorAsDecimalSerializer::class) RGB>? = null,
	var fadeColors: List<@Serializable(RGB.Companion.ColorAsDecimalSerializer::class) RGB>? = null,
	var hasTrail: Boolean? = null,
	var hasFlicker: Boolean? = null,
) : ItemFunction()

fun ItemModifier.setFireworkExplosion(shape: FireworkExplosionShape, block: SetFireworkExplosion.() -> Unit = {}) {
	modifiers += SetFireworkExplosion(shape = shape).apply(block)
}
