package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.SwingAnimationType
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:attack_animation` item component, which sets the arm-swing animation played when attacking with this item.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#attack_animation
 *
 * @property duration The animation length in ticks, `0` or more.
 * @property type The animation, [SwingAnimationType.NONE] plays none.
 */
@Serializable
data class AttackAnimationComponent(
	var duration: Int? = null,
	var type: SwingAnimationType? = null,
) : Component()

/** Sets the arm-swing animation played when attacking with this item. */
fun ComponentsScope.attackAnimation(block: AttackAnimationComponent.() -> Unit = {}) = apply {
	this[ItemComponentTypes.ATTACK_ANIMATION] = AttackAnimationComponent().apply(block)
}

/**
 * Sets the arm-swing animation played when attacking with this item.
 *
 * ```
 * attackAnimation(SwingAnimationType.STAB, duration = 10)  // attack_animation={duration:10,type:"stab"}
 * ```
 */
fun ComponentsScope.attackAnimation(type: SwingAnimationType? = null, duration: Int? = null) = apply {
	this[ItemComponentTypes.ATTACK_ANIMATION] = AttackAnimationComponent(duration, type)
}
