package io.github.ayfri.kore.data.item

import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.features.predicates.sub.ItemStackPredicate
import kotlinx.serialization.Serializable

@Serializable
data class ItemStack(
	val id: String,
	val count: Short? = null,
	val components: Components? = null,
) {
	constructor(itemArgument: ItemArgument, count: Short? = null) : this(
		itemArgument.asId(),
		count,
		itemArgument.components?.toComponents()
	)

	fun toItemArgument() = ItemArgument(id.substringAfter(":"), id.substringBefore(":"), components?.toPatch())

	fun toItemStackPredicate() = ItemStackPredicate(
		count = count?.toInt()?.let { rangeOrInt(it) },
		components = components,
		items = listOf(toItemArgument())
	)
}
