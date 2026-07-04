package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Represents the `minecraft:damage` item component, which sets how much durability a damageable item has already consumed.
 *
 * Serializes as the integer damage directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#damage
 */
@Serializable(with = DamageComponent.Companion.DamageComponentSerializer::class)
data class DamageComponent(var damage: Int) : Component() {
	companion object {
		data object DamageComponentSerializer :
			InlineAutoSerializer<DamageComponent, Int>(serializer<Int>(), DamageComponent::damage, ::DamageComponent)
	}
}

/** Sets the current damage/durability consumed on a damageable item. */
fun ComponentsScope.damage(damage: Int) = apply { this[ItemComponentTypes.DAMAGE] = DamageComponent(damage) }
