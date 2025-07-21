package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.Serializable

@Serializable
data class UseRemainderComponent(
	var item: ItemStack,
) : Component()

fun ComponentsScope.useRemainder(item: ItemStack) = apply {
	this[ItemComponentTypes.USE_REMAINDER] = UseRemainderComponent(item)
}
