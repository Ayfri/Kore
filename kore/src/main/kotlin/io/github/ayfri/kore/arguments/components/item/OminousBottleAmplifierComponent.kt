package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = OminousBottleAmplifierComponent.Companion.OminousBottleAmplifierComponentSerializer::class)
data class OminousBottleAmplifierComponent(var amplifier: Int) : Component() {
	companion object {
		data object OminousBottleAmplifierComponentSerializer : InlineAutoSerializer<OminousBottleAmplifierComponent>(
			OminousBottleAmplifierComponent::class
		)
	}
}

fun ComponentsScope.ominousBottleAmplifier(amplifier: Int) =
	apply { this[ItemComponentTypes.OMINOUS_BOTTLE_AMPLIFIER] = OminousBottleAmplifierComponent(amplifier) }
