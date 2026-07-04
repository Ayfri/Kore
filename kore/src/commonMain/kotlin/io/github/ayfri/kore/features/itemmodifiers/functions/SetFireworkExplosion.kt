package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.colors.RGB
import io.github.ayfri.kore.arguments.components.item.FireworkExplosionShape
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

/**
 * Appends a single firework explosion to the item's firework component. Mirrors `minecraft:set_firework_explosion`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetFireworkExplosion(
	override var conditions: PredicateAsList? = null,
	var shape: FireworkExplosionShape,
	var colors: List<@Serializable(RGB.Companion.ColorAsDecimalSerializer::class) RGB>? = null,
	var fadeColors: List<@Serializable(RGB.Companion.ColorAsDecimalSerializer::class) RGB>? = null,
	var hasTrail: Boolean? = null,
	var hasFlicker: Boolean? = null,
) : ItemFunction()

/** Add a `set_firework_explosion` step. */
fun ItemModifier.setFireworkExplosion(shape: FireworkExplosionShape, block: SetFireworkExplosion.() -> Unit = {}) {
	modifiers += SetFireworkExplosion(shape = shape).apply(block)
}
