package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = PotionDurationScaleComponent.Companion.PotionDurationScaleComponentSerializer::class)
data class PotionDurationScaleComponent(var value: Float) : Component() {
	companion object {
		data object PotionDurationScaleComponentSerializer : InlineAutoSerializer<PotionDurationScaleComponent>(
			PotionDurationScaleComponent::class
		)
	}
}

fun ComponentsScope.potionDurationScale(value: Float) =
	apply { this[ItemComponentTypes.POTION_DURATION_SCALE] = PotionDurationScaleComponent(value) }
