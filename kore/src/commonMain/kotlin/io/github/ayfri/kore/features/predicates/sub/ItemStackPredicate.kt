package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.components.matchers.DataComponentPredicate
import io.github.ayfri.kore.arguments.numbers.ranges.asRangeOrInt
import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Matches an item stack: which item it is, how many there are, and the values of its data components.
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@Serializable
data class ItemStackPredicate(
	var count: IntRangeOrIntJson? = null,
	var items: InlinableList<ItemOrTagArgument>? = null,
	var components: Components? = null,
	var predicates: DataComponentPredicate? = null,
)

/** Creates an [ItemStackPredicate] matching any of [items]. */
fun itemStackPredicate(vararg items: ItemOrTagArgument, init: ItemStackPredicate.() -> Unit = {}) =
	ItemStackPredicate(items = items.toList().ifEmpty { null }).apply(init)

/** Restricts this predicate to the given [items]. */
fun ItemStackPredicate.items(vararg items: ItemOrTagArgument) {
	this.items = items.toList()
}

/** Matches an exact stack size. */
fun ItemStackPredicate.count(value: Int) {
	count = value.asRangeOrInt()
}

/** Matches a stack size within [range]. */
fun ItemStackPredicate.count(range: IntRange) {
	count = range.asRangeOrInt()
}

/** Matches exact data component values on the stack. */
fun ItemStackPredicate.components(block: Components.() -> Unit) {
	components = Components().apply(block)
}

/** Tests data component values on the stack. */
fun ItemStackPredicate.predicates(block: DataComponentPredicate.() -> Unit) {
	predicates = DataComponentPredicate().apply(block)
}
