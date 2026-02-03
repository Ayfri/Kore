package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

/**
 * Base type for all item functions (vanilla loot functions) supported by Kore.
 *
 * Each subclass maps to one `minecraft:<function>` entry and supports an optional list of
 * predicate conditions. Functions are collected by [ItemModifier] and serialised to the
 * vanilla JSON format.
 *
 * Documentation: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable(with = ItemFunction.Companion.ItemFunctionSerializer::class)
sealed class ItemFunction {
    /** Optional predicate conditions guarding this function. */
    abstract var conditions: PredicateAsList?

	companion object {
		data object ItemFunctionSerializer : NamespacedPolymorphicSerializer<ItemFunction>(ItemFunction::class, outputName = "function")
	}
}

/**
 * Define the predicate conditions that must pass for this item function to apply.
 *
 * See Predicates in the docs page: https://kore.ayfri.com/docs/data-driven/item-modifiers
 */
fun ItemFunction.conditions(block: Predicate.() -> Unit) {
	conditions = Predicate().apply(block)
}
