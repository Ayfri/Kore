package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.ConsumeCooldownGroupArgument
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:use_cooldown` item component, which applies a shared use cooldown to items in the same cooldown group.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#use_cooldown
 */
@Serializable
data class UseCooldownComponent(
	var seconds: Float,
	@SerialName("cooldown_group")
	var cooldownGroup: ConsumeCooldownGroupArgument,
) : Component()

/** Applies a shared use cooldown to items in the same cooldown group. */
fun ComponentsScope.useCooldown(
	seconds: Float,
	cooldownGroup: ConsumeCooldownGroupArgument,
	block: UseCooldownComponent.() -> Unit = {},
) = apply {
	this[ItemComponentTypes.USE_COOLDOWN] = UseCooldownComponent(seconds, cooldownGroup).apply(block)
}

fun ComponentsScope.useCooldown(
	seconds: Float,
	cooldownGroup: String,
	block: UseCooldownComponent.() -> Unit = {},
) = apply {
	this[ItemComponentTypes.USE_COOLDOWN] = UseCooldownComponent(seconds, ConsumeCooldownGroupArgument(cooldownGroup)).apply(block)
}
