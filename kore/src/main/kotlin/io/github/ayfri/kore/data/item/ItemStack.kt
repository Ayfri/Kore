package io.github.ayfri.kore.data.item

import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import kotlinx.serialization.Serializable
import io.github.ayfri.kore.features.predicates.sub.ItemStack as ItemStackSubPredicate

@Serializable
data class ItemStack(
	val id: String,
	val count: Short? = null,
	val components: Components? = null,
) {
	constructor(itemArgument: ItemArgument, count: Short? = null) : this(itemArgument.asId(), count, itemArgument.components)

	fun toItemArgument() = ItemArgument(id.substringBefore(":"), id.substringAfter(":"), components)

	fun toItemStackSubPredicate() = ItemStackSubPredicate(
		count = count?.toInt()?.let { rangeOrInt(it) },
		components = components,
		items = listOf(toItemArgument())
	)
}
