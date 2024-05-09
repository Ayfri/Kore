package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.features.predicates.sub.item.ItemStackSubPredicates
import io.github.ayfri.kore.generated.ComponentTypes
import kotlin.reflect.full.memberProperties

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
 *   negate(ComponentTypes.DAMAGE)
 *   clearPredicate(ComponentTypes.DAMAGE)
 * }
 * ```
 * Will be serialized as: `*`
 */
fun ItemPredicate.clearPredicate(component: ComponentTypes) = clearPredicate(component.name.lowercase())

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
	if (subPredicatesKeys.contains(component)) {
		subPredicates.removeIf { subPredicate ->
			subPredicate::class.memberProperties.any { member ->
				member.getter.call(subPredicate) != null
			}
		}
	}
}

/**
 * Add a check to see if the component is present.
 *
 * Example:
 * ```kotlin
 * itemPredicate {
 *   isPresent(ComponentTypes.DAMAGE)
 * }
 * ```
 * Will be serialized as: `*[damage]`
 */
fun ItemPredicate.isPresent(vararg component: ComponentTypes) = component.forEach(::setExpected)

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
 * Set the last added component as negated.
 * **Negated isn't similar to removed
 * - Negated means that the value should not be equal to the one set.
 * - Removed means that the component should not be present.
 *
 * Example:
 * ```kotlin
 * itemPredicate {
 *    !damage(1)
 *    damage(2)
 * }
 *
 * // Will output: `*[!damage=1|damage=2]`
 */
fun ItemPredicate.negate(vararg component: ComponentTypes) = component.forEach(::setNegated)

/**
 * Set the last added component as negated.
 * **Negated isn't similar to removed
 * - Negated means that the value should not be equal to the one set.
 * - Removed means that the component should not be present.
 *
 * Example:
 * ```kotlin
 * itemPredicate {
 *    !damage(1)
 *    damage(2)
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
 * Just an infix method to make the code more readable and so you can use `or` instead of adding one line for each predicate.
 */
infix fun ItemPredicate.or(predicate: ItemPredicate) = this
fun ItemArgument.predicate(block: ItemPredicate.() -> Unit) = ItemPredicate(this).apply(block)
fun itemPredicate(block: ItemPredicate.() -> Unit) = ItemPredicate().apply(block)
