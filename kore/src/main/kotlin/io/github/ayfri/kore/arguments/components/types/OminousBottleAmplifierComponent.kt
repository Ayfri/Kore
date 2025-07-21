package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

@Serializable(with = OminousBottleAmplifierComponent.Companion.OminousBottleAmplifierComponentSerializer::class)
data class OminousBottleAmplifierComponent(
	var amplifier: Int,
) : Component() {
	companion object {
		object OminousBottleAmplifierComponentSerializer : InlineSerializer<OminousBottleAmplifierComponent, Int>(
			Int.serializer(),
			OminousBottleAmplifierComponent::amplifier
		)
	}
}

fun ComponentsScope.ominousBottleAmplifier(amplifier: Int) =
	apply { this[ItemComponentTypes.OMINOUS_BOTTLE_AMPLIFIER] = OminousBottleAmplifierComponent(amplifier) }
