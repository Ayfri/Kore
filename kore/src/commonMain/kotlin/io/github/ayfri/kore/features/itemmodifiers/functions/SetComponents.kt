package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.components.ComponentsPatch
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

/**
 * Applies a component patch to the target item.
 *
 * This maps to the vanilla `minecraft:set_components` function and accepts a [ComponentsPatch]
 * containing additions and removals (`!component`) to apply atomically.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetComponents(
	override var conditions: PredicateAsList? = null,
	val components: ComponentsPatch,
) : ItemFunction()

/** Add a `set_components` step to this modifier. */
fun ItemModifier.setComponents(components: ComponentsPatch.() -> Unit = {}) =
    SetComponents(components = ComponentsPatch().apply(components)).also { modifiers += it }
