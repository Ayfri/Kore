package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.ConsumeCooldownGroupArgument
import io.github.ayfri.kore.generated.ComponentTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UseCooldownComponent(
	var seconds: Float,
	@SerialName("cooldown_group")
	var cooldownGroup: ConsumeCooldownGroupArgument,
) : Component()

fun ComponentsScope.useCooldown(
	seconds: Float,
	cooldownGroup: ConsumeCooldownGroupArgument,
	block: UseCooldownComponent.() -> Unit = {},
) = apply {
	this[ComponentTypes.USE_COOLDOWN] = UseCooldownComponent(seconds, cooldownGroup).apply(block)
}

fun ComponentsScope.useCooldown(
	seconds: Float,
	cooldownGroup: String,
	block: UseCooldownComponent.() -> Unit = {},
) = apply {
	this[ComponentTypes.USE_COOLDOWN] = UseCooldownComponent(seconds, ConsumeCooldownGroupArgument(cooldownGroup)).apply(block)
}
