package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.generated.ComponentTypes
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class RepairableComponent(
	var items: InlinableList<ItemOrTagArgument> = listOf(),
) : Component()

fun ComponentsScope.repairable(vararg items: ItemOrTagArgument) = apply {
	this[ComponentTypes.REPAIRABLE] = RepairableComponent(items.toList())
}
