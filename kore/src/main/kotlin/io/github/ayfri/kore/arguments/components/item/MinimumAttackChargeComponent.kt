package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = MinimumAttackChargeComponent.Companion.MinimumAttackChargeComponentSerializer::class)
data class MinimumAttackChargeComponent(
	var value: Float,
) : Component() {
	companion object {
		data object MinimumAttackChargeComponentSerializer : InlineAutoSerializer<MinimumAttackChargeComponent>(
			MinimumAttackChargeComponent::class
		)
	}
}

fun ComponentsScope.minimumAttackCharge(value: Float) = apply {
	this[ItemComponentTypes.MINIMUM_ATTACK_CHARGE] = MinimumAttackChargeComponent(value)
}
