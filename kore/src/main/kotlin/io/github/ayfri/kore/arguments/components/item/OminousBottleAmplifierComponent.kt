package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:ominous_bottle_amplifier` item component, which sets the Bad Omen effect amplifier (0-4) when consuming an ominous bottle.
 *
 * Serializes as the integer amplifier directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#ominous_bottle_amplifier
 */
@Serializable(with = OminousBottleAmplifierComponent.Companion.OminousBottleAmplifierComponentSerializer::class)
data class OminousBottleAmplifierComponent(var amplifier: Int) : Component() {
	companion object {
		data object OminousBottleAmplifierComponentSerializer : InlineAutoSerializer<OminousBottleAmplifierComponent>(
			OminousBottleAmplifierComponent::class
		)
	}
}

/** Sets the Bad Omen effect amplifier (0-4) when consuming an ominous bottle. */
fun ComponentsScope.ominousBottleAmplifier(amplifier: Int) =
	apply { this[ItemComponentTypes.OMINOUS_BOTTLE_AMPLIFIER] = OminousBottleAmplifierComponent(amplifier) }
