package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.SwingAnimationType
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:interact_animation` item component, which sets the arm-swing animation played when using (right-clicking) this item.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#interact_animation
 */
@Serializable
data class InteractAnimationComponent(
	var duration: Int? = null,
	var type: SwingAnimationType? = null,
) : Component()

/** Sets the arm-swing animation played when using (right-clicking) this item. */
fun ComponentsScope.interactAnimation(block: InteractAnimationComponent.() -> Unit = {}) = apply {
	this[ItemComponentTypes.INTERACT_ANIMATION] = InteractAnimationComponent().apply(block)
}

/**
 * Sets the arm-swing animation played when using (right-clicking) this item.
 *
 * ```
 * interactAnimation(SwingAnimationType.WHACK, duration = 4)  // interact_animation={duration:4,type:"whack"}
 * ```
 */
fun ComponentsScope.interactAnimation(type: SwingAnimationType? = null, duration: Int? = null) = apply {
	this[ItemComponentTypes.INTERACT_ANIMATION] = InteractAnimationComponent(duration, type)
}
