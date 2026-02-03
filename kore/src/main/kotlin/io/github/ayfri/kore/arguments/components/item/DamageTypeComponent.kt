package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import io.github.ayfri.kore.generated.DamageTypes
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.DamageTypeArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = DamageTypeComponent.Companion.DamageTypeComponentSerializer::class)
data class DamageTypeComponent(
	@Serializable(with = ResourceLocationArgument.Companion.ResourceLocationArgumentSimpleSerializer::class)
	var value: DamageTypeArgument,
) : Component() {
	companion object {
		data object DamageTypeComponentSerializer : InlineAutoSerializer<DamageTypeComponent>(DamageTypeComponent::class)
	}
}

fun ComponentsScope.damageType(value: DamageTypeArgument) = apply {
	this[ItemComponentTypes.DAMAGE_TYPE] = DamageTypeComponent(value)
}

fun ComponentsScope.damageType(value: DamageTypes) = damageType(value as DamageTypeArgument)

fun ComponentsScope.damageType(value: String, namespace: String = "minecraft") = damageType(DamageTypeArgument(value, namespace))
