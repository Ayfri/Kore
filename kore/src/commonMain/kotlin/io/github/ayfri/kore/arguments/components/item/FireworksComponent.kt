package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:fireworks` item component, which configures firework rocket flight duration and explosion effects.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#fireworks
 */
@Serializable
data class FireworksComponent(
	var explosions: List<FireworkExplosionComponent> = emptyList(),
	@SerialName("flight_duration")
	var flightDuration: Int,
) : Component()

/** Configures firework rocket flight duration and explosion effects. */
fun ComponentsScope.fireworks(explosions: List<FireworkExplosionComponent>, flightDuration: Int) = apply {
	this[ItemComponentTypes.FIREWORKS] = FireworksComponent(explosions, flightDuration)
}

fun ComponentsScope.fireworks(flightDuration: Int, block: FireworksComponent.() -> Unit) = apply {
	this[ItemComponentTypes.FIREWORKS] = FireworksComponent(mutableListOf(), flightDuration).apply(block)
}

fun FireworksComponent.explosion(shape: FireworkExplosionShape, block: FireworkExplosionComponent.() -> Unit) = apply {
	explosions += FireworkExplosionComponent(shape).apply(block)
}
