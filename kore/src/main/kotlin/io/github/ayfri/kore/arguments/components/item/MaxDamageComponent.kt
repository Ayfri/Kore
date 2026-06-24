package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:max_damage` item component, which sets the maximum durability before the item breaks.
 *
 * Serializes as the integer value directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#max_damage
 */
@Serializable(with = MaxDamageComponent.Companion.MaxDamageComponentSerializer::class)
data class MaxDamageComponent(var maxDamage: Int) : Component() {
	companion object {
		data object MaxDamageComponentSerializer : InlineAutoSerializer<MaxDamageComponent>(MaxDamageComponent::class)
	}
}

/** Sets the maximum durability before the item breaks. */
fun ComponentsScope.maxDamage(maxStackSize: Int) = apply { this[ItemComponentTypes.MAX_DAMAGE] = MaxDamageComponent(maxStackSize) }
