package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class RepairableComponent(
	var items: InlinableList<ItemOrTagArgument> = listOf(),
) : Component()

fun ComponentsScope.repairable(vararg items: ItemOrTagArgument) = apply {
	this[ItemComponentTypes.REPAIRABLE] = RepairableComponent(items.toList())
}
