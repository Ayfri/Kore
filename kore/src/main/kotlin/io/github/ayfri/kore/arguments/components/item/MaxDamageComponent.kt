package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = MaxDamageComponent.Companion.MaxDamageComponentSerializer::class)
data class MaxDamageComponent(var maxDamage: Int) : Component() {
	companion object {
		data object MaxDamageComponentSerializer : InlineAutoSerializer<MaxDamageComponent>(MaxDamageComponent::class)
	}
}

fun ComponentsScope.maxDamage(maxStackSize: Int) = apply { this[ItemComponentTypes.MAX_DAMAGE] = MaxDamageComponent(maxStackSize) }
