package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.features.predicates.sub.item.ItemStackSubPredicates
import io.github.ayfri.kore.generated.ItemComponentTypes
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.buildNbtCompound

fun itemPredicate(block: ItemPredicate.() -> Unit = {}) = ItemPredicate().apply(block)
fun ItemArgument.predicate(block: ItemPredicate.() -> Unit = {}) = ItemPredicate(this).apply(block)

/**
 * Just an infix method to make the code more readable and so you can use `or` instead of adding one line for each predicate.
 */
infix fun ItemPredicate.or(predicate: ItemPredicate) = this


fun ItemPredicate.count(range: IntRange) = apply {
	countSubPredicates += rangeOrInt(range) to false
}

fun ItemPredicate.count(value: Int) = apply {
	countSubPredicates += rangeOrInt(value) to false
}

fun ItemPredicate.count(range: io.github.ayfri.kore.arguments.numbers.ranges.IntRange) {
	if (range.start != null && range.end != null) count(range.start..range.end)
	else error("The range must have a start and an end.")
}

/**
 * Remove all the predicates of the given component.
 *
 * Example:
 * ```kotlin
 * itemPredicate {
 *   damage(4)
 *   damage(1)
 *   negate(ItemComponentTypes.DAMAGE)
 *   clearPredicate(ItemComponentTypes.DAMAGE)
 * }
 * ```
 * Will be serialized as: `*`
 */
fun ItemPredicate.clearPredicate(component: ItemComponentTypes) = clearPredicate(component.name.lowercase())

/**
 * Remove all the predicates of the given component.
 *
 * Example:
 * ```kotlin
 * itemPredicate {
 *   count(1..5)
 *   count(1)
 *   negate("count")
 *   clearPredicate("count")
 * }
 * ```
 * Will be serialized as: `*`
 */
fun ItemPredicate.clearPredicate(component: String) {
	componentsAlternatives.remove(component)
	componentsAlternatives.remove("!$component")
	if (component == COUNT_ITEM_PREDICATE) countSubPredicates.clear()
	if (component in subPredicatesKeys) {
		subPredicates.removeIf { (matchers) ->
			matchers.any { it.componentName == component }
		}
	}
}

/**
 * Add a check to see if the component is present.
 *
 * Example:
 * ```kotlin
 * itemPredicate {
 *   isPresent(ItemComponentTypes.DAMAGE)
 * }
 * ```
 * Will be serialized as: `*[damage]`
 */
fun ItemPredicate.isPresent(vararg component: ItemComponentTypes) = component.forEach(::setExpected)

/**
 * Add a check to see if the component is present.
 *
 * Example:
 * ```kotlin
 * itemPredicate {
 *   isPresent("damage")
 * }
 * ```
 * Will be serialized as: `*[damage]`
 */
fun ItemPredicate.isPresent(vararg component: String) = component.forEach(::setExpected)

/**
 * Set one or multiple item components to be negated.
 * **Negated isn't similar to removed**
 * - Negated means that the value should not be equal to the one set.
 * - Removed means that the component should not be present.
 *
 * Example:
 * ```kotlin
 * itemPredicate {
 *    damage(1)
 *    damage(2)
 *
 *    negate(ItemComponentTypes.DAMAGE)
 * }
 *
 * // Will output: `*[!damage=1|damage=2]`
 */
fun ItemPredicate.negate(vararg component: ItemComponentTypes) = component.forEach(::setNegated)

/**
 * Set one or multiple item components to be negated.
 * **Negated isn't similar to removed**
 * - Negated means that the value should not be equal to the one set.
 * - Removed means that the component should not be present.
 *
 * Example:
 * ```kotlin
 * itemPredicate {
 *    damage(1)
 *    damage(2)
 *
 *    negate("damage")
 * }
 *
 * // Will output: `*[!damage=1|damage=2]`
 */
fun ItemPredicate.negate(vararg component: String) = component.forEach(::setNegated)

/**
 * Add a sub predicate to the item.
 */
fun ItemPredicate.subPredicates(block: ItemStackSubPredicates.() -> Unit) = apply {
	subPredicates += ItemStackSubPredicates().apply(block)
}

/**
 * Set the components as partial.
 *
 * Example:
 * ```kotlin
 * itemPredicate {
 *   customData {
 *     this["test"] = 1
 *   }
 *   partial(ItemComponentTypes.CUSTOM_DATA)
 * }
 * ```
 * Will be serialized as: `*[custom_data~{test:1}]`
 */
fun ItemPredicate.partial(vararg component: ItemComponentTypes) = component.forEach(::setPartial)

/**
 * Set the components as partial.
 *
 * Example:
 * ```kotlin
 * itemPredicate {
 *   customData {
 *     this["test"] = 1
 *   }
 *   partial("custom_data")
 * }
 * ```
 * Will be serialized as: `*[custom_data~{test:1}]`
 */
fun ItemPredicate.partial(vararg component: String) = component.forEach(::setPartial)

/**
 * Create a custom component as a partial.
 * This is useful when you want to match a classic component but skipping some required fields of the class.
 *
 * Example:
 * ```kotlin
 * itemPredicate {
 *   buildPartial(ItemComponentTypes.CUSTOM_DATA) {
 *     this["test"] = 1
 *   }
 * }
 * ```
 * Will be serialized as: `*[custom_data~{test:1}]`
 */
fun ItemPredicate.buildPartial(component: ItemComponentTypes, nbtBuilder: NbtCompoundBuilder.() -> Unit) =
	buildPartial(component.name.lowercase(), nbtBuilder)


/**
 * Create a custom component as a partial.
 * This is useful when you want to match a classic component but skipping some required fields of the class.
 *
 * Example:
 * ```kotlin
 * itemPredicate {
 *   buildPartial("my_data") {
 *     this["test"] = 1
 *   }
 * }
 * ```
 * Will be serialized as: `*[my_data~{test:1}]`
 */
fun ItemPredicate.buildPartial(name: String, nbtBuilder: NbtCompoundBuilder.() -> Unit) = apply {
	this["~$name"] = buildNbtCompound(nbtBuilder)
}

/**
 * Alias for [isPresent] - check if a component exists on an item.
 * Both `*[component]` and `*[component~{}]` are equivalent in Minecraft,
 * so this serializes as `*[component]` for brevity.
 *
 * Example:
 * ```kotlin
 * itemPredicate {
 *   existsPartial(ItemComponentTypes.INSTRUMENT)
 * }
 * ```
 * Will be serialized as: `*[instrument]`
 */
fun ItemPredicate.existsPartial(component: ItemComponentTypes) = isPresent(component)

/**
 * Alias for [isPresent] - check if a component exists on an item.
 * Both `*[component]` and `*[component~{}]` are equivalent in Minecraft,
 * so this serializes as `*[component]` for brevity.
 *
 * Example:
 * ```kotlin
 * itemPredicate {
 *   existsPartial("instrument")
 * }
 * ```
 * Will be serialized as: `*[instrument]`
 */
fun ItemPredicate.existsPartial(name: String) = isPresent(name)
