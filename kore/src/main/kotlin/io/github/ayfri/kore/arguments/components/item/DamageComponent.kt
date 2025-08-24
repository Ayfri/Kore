package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = DamageComponent.Companion.DamageComponentSerializer::class)
data class DamageComponent(var damage: Int) : Component() {
	companion object {
		data object DamageComponentSerializer : InlineAutoSerializer<DamageComponent>(DamageComponent::class)
	}
}

fun ComponentsScope.damage(damage: Int) = apply { this[ItemComponentTypes.DAMAGE] = DamageComponent(damage) }
