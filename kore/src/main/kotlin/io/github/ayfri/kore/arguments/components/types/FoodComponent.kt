package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FoodComponent(
	var nutrition: Float,
	var saturation: Float,
	@SerialName("can_always_eat")
	var canAlwaysEat: Boolean? = null,
) : Component()

fun ComponentsScope.food(
	nutrition: Float,
	saturation: Float,
	canAlwaysEat: Boolean? = null,
	block: FoodComponent.() -> Unit = {},
) = apply {
	this[ItemComponentTypes.FOOD] = FoodComponent(nutrition, saturation, canAlwaysEat).apply(block)
}
