package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.annotations.LootDsl
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.providers.NumberProviderScope
import io.github.ayfri.kore.features.predicates.toCondition
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

/**
 * Base type for all item functions (vanilla loot functions) supported by Kore.
 *
 * Each subclass maps to one `minecraft:<type>` entry, serialized as `{ "type": "minecraft:<type>", ... }`, and supports
 * an optional [condition] guarding it. Functions are collected by [ItemModifier] and serialised to the
 * vanilla JSON format.
 *
 * Documentation: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@GeneratedSealedSerializer
@LootDsl
@Serializable(with = ItemFunction.Companion.ItemFunctionSerializer::class)
sealed class ItemFunction : NumberProviderScope {
	/** Optional predicate condition guarding this function. */
	abstract var condition: PredicateCondition?

	companion object {
		data object ItemFunctionSerializer : NamespacedPolymorphicSerializer<ItemFunction>(itemFunctionSealedSerializer())
	}
}

/**
 * Define the predicate condition that must pass for this item function to apply, several conditions being
 * wrapped in an `all_of`.
 *
 * ```
 * setCount(1f) { condition { randomChance(0.5f) } }  // "condition": { "type": "minecraft:random_chance", ... }
 * ```
 *
 * See Predicates in the docs page: https://kore.ayfri.com/docs/data-driven/item-modifiers
 */
fun ItemFunction.condition(block: Predicate.() -> Unit) {
	condition = Predicate().apply(block).toCondition()
}
