package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UseEffectsComponent(
	@SerialName("can_sprint")
	var canSprint: Boolean? = null,
	@SerialName("speed_multiplier")
	var speedMultiplier: Float? = null,
) : Component()

fun ComponentsScope.useEffects(block: UseEffectsComponent.() -> Unit = {}) = apply {
	this[ItemComponentTypes.USE_EFFECTS] = UseEffectsComponent().apply(block)
}

fun ComponentsScope.useEffects(canSprint: Boolean? = null, speedMultiplier: Float? = null) = apply {
	this[ItemComponentTypes.USE_EFFECTS] = UseEffectsComponent(canSprint, speedMultiplier)
}
