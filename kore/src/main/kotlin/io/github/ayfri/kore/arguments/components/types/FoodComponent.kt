package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.resources.EffectArgument
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.generated.ComponentTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FoodEffect(
	var effect: Effect,
	var probability: Float,
)

@Serializable
data class FoodComponent(
	var nutrition: Int,
	var saturation: Float,
	@SerialName("is_meat")
	var isMeat: Boolean? = null,
	@SerialName("can_always_eat")
	var canAlwaysEat: Boolean? = null,
	@SerialName("eat_seconds")
	var eatSeconds: Float? = null,
	var effects: List<FoodEffect>? = null,
	@SerialName("using_converts_to")
	var usingConvertsTo: ItemStack? = null,
) : Component()

fun ComponentsScope.food(
	nutrition: Int,
	saturation: Float,
	isMeat: Boolean? = null,
	canAlwaysEat: Boolean? = null,
	eatSeconds: Float? = null,
	effects: List<FoodEffect>,
	block: FoodComponent.() -> Unit = {},
) = apply {
	this[ComponentTypes.FOOD] = FoodComponent(nutrition, saturation, isMeat, canAlwaysEat, eatSeconds, effects).apply(block)
}

fun ComponentsScope.food(
	nutrition: Int,
	saturation: Float,
	isMeat: Boolean? = null,
	canAlwaysEat: Boolean? = null,
	eatSeconds: Float? = null,
	vararg effects: FoodEffect,
	block: FoodComponent.() -> Unit = {},
) = food(nutrition, saturation, isMeat, canAlwaysEat, eatSeconds, effects.toList(), block)

fun FoodComponent.effect(probability: Float, effect: Effect) = apply {
	effects = (effects ?: mutableListOf()) + FoodEffect(effect, probability)
}

fun FoodComponent.effect(
	probability: Float,
	id: EffectArgument,
	duration: Int,
	amplifier: Byte,
	ambient: Boolean,
	showParticles: Boolean,
	showIcon: Boolean,
) = apply {
	effects = (effects ?: mutableListOf()) + FoodEffect(Effect(id, duration, amplifier, ambient, showParticles, showIcon), probability)
}

fun FoodComponent.convertsTo(id: ItemArgument, block: ItemStack.() -> Unit = {}) = apply {
	usingConvertsTo = ItemStack(id).apply(block)
}
