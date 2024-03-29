package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

@Serializable(with = DamageComponent.Companion.DamageComponentSerializer::class)
data class DamageComponent(var damage: Int) : Component() {
	companion object {
		object DamageComponentSerializer : InlineSerializer<DamageComponent, Int>(Int.serializer(), DamageComponent::damage)
	}
}

fun Components.damage(damage: Int) = apply { components["damage"] = DamageComponent(damage) }
