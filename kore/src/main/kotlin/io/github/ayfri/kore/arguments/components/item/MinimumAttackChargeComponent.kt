package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Represents the `minecraft:minimum_attack_charge` item component, which sets the minimum attack charge (0.0-1.0) required for full damage.
 *
 * Serializes as the float value directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#minimum_attack_charge
 */
@Serializable(with = MinimumAttackChargeComponent.Companion.MinimumAttackChargeComponentSerializer::class)
data class MinimumAttackChargeComponent(
	var value: Float,
) : Component() {
	companion object {
		data object MinimumAttackChargeComponentSerializer : InlineAutoSerializer<MinimumAttackChargeComponent, Float>(
			serializer<Float>(),
			MinimumAttackChargeComponent::value,
			::MinimumAttackChargeComponent
		)
	}
}

/** Sets the minimum attack charge (0.0-1.0) required for full damage. */
fun ComponentsScope.minimumAttackCharge(value: Float) = apply {
	this[ItemComponentTypes.MINIMUM_ATTACK_CHARGE] = MinimumAttackChargeComponent(value)
}
