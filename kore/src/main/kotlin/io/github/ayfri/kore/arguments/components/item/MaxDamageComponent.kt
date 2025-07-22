package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

@Serializable(with = MaxDamageComponent.Companion.MaxDamageComponentSerializer::class)
data class MaxDamageComponent(var maxDamage: Int) : Component() {
	companion object {
		object MaxDamageComponentSerializer : InlineSerializer<MaxDamageComponent, Int>(Int.serializer(), MaxDamageComponent::maxDamage)
	}
}

fun ComponentsScope.maxDamage(maxStackSize: Int) = apply { this[ItemComponentTypes.MAX_DAMAGE] = MaxDamageComponent(maxStackSize) }
