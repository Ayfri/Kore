package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:use_effects` item component, which configures player behavior (sprint, vibrations, speed) while using the item.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#use_effects
 */
@Serializable
data class UseEffectsComponent(
	@SerialName("can_sprint")
	var canSprint: Boolean? = null,
	@SerialName("interact_vibrations")
	var interactVibrations: Boolean? = null,
	@SerialName("speed_multiplier")
	var speedMultiplier: Float? = null,
) : Component()

/** Configures player behavior (sprint, vibrations, speed) while using the item. */
fun ComponentsScope.useEffects(block: UseEffectsComponent.() -> Unit = {}) = apply {
	this[ItemComponentTypes.USE_EFFECTS] = UseEffectsComponent().apply(block)
}

fun ComponentsScope.useEffects(canSprint: Boolean? = null, interactVibrations: Boolean? = null, speedMultiplier: Float? = null) =
	apply {
		this[ItemComponentTypes.USE_EFFECTS] = UseEffectsComponent(canSprint, interactVibrations, speedMultiplier)
}
