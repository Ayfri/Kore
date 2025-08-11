package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.generated.arguments.types.PotionArgument
import kotlinx.serialization.Serializable

/**
 * Sets suspicious-stew effects. Mirrors `minecraft:set_stew_effect`.
 * Each entry defines an effect id and a duration as a number provider.
 *
 * Docs: https://kore.ayfri.com/docs/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetStewEffect(
	override var conditions: PredicateAsList? = null,
	val effects: List<PotionEffect>,
) : ItemFunction()

/** Single stew effect entry. */
@Serializable
data class PotionEffect(
	var type: PotionArgument,
	var duration: NumberProvider = constant(0f),
)

/** Add a `set_stew_effect` step via a builder list. */
fun ItemModifier.setStewEffect(effects: MutableList<PotionEffect>.() -> Unit) =
	SetStewEffect(effects = buildList(effects)).also { modifiers += it }

/** Add a single-entry `set_stew_effect` step. */
fun ItemModifier.setStewEffect(type: PotionArgument, duration: NumberProvider = constant(0f), block: SetStewEffect.() -> Unit = {}) {
	modifiers += SetStewEffect(
		effects = listOf(
			PotionEffect(
				type,
				duration
			)
		)
	).apply(block)
}

/** Append one stew effect entry to the builder list. */
fun MutableList<PotionEffect>.potionEffect(type: PotionArgument, duration: NumberProvider = constant(0f)) {
	add(PotionEffect(type, duration))
}
