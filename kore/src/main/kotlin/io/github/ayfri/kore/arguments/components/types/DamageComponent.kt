package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

@Serializable(with = DamageComponent.Companion.DamageComponentSerializer::class)
data class DamageComponent(var damage: Int) : Component() {
	companion object {
		object DamageComponentSerializer : InlineSerializer<DamageComponent, Int>(Int.serializer(), DamageComponent::damage)
	}
}

fun ComponentsScope.damage(damage: Int) = apply { this[ItemComponentTypes.DAMAGE] = DamageComponent(damage) }
