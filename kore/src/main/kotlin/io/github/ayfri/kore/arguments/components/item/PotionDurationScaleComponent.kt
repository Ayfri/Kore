package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:potion_duration_scale` item component, which multiplies the duration of potion effects from this item.
 *
 * Serializes as the float value directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#potion_duration_scale
 */
@Serializable(with = PotionDurationScaleComponent.Companion.PotionDurationScaleComponentSerializer::class)
data class PotionDurationScaleComponent(var value: Float) : Component() {
	companion object {
		data object PotionDurationScaleComponentSerializer : InlineAutoSerializer<PotionDurationScaleComponent>(
			PotionDurationScaleComponent::class
		)
	}
}

/** Multiplies the duration of potion effects from this item. */
fun ComponentsScope.potionDurationScale(value: Float) =
	apply { this[ItemComponentTypes.POTION_DURATION_SCALE] = PotionDurationScaleComponent(value) }
