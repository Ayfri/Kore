package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

@Serializable(with = PotionDurationScaleComponent.Companion.PotionDurationScaleComponentSerializer::class)
data class PotionDurationScaleComponent(var value: Float) : Component() {
	companion object {
		object PotionDurationScaleComponentSerializer : InlineSerializer<PotionDurationScaleComponent, Float>(
			Float.serializer(),
			PotionDurationScaleComponent::value
		)
	}
}

fun ComponentsScope.potionDurationScale(value: Float) =
	apply { this[ItemComponentTypes.POTION_DURATION_SCALE] = PotionDurationScaleComponent(value) }
