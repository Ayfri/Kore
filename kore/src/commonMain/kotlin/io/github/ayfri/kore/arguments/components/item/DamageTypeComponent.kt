package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import io.github.ayfri.kore.generated.DamageTypes
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.DamageTypeArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:damage_type` item component, which specifies the damage type dealt when attacking with this item.
 *
 * Serializes as the damage type id directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#damage_type
 */
@Serializable(with = DamageTypeComponent.Companion.DamageTypeComponentSerializer::class)
data class DamageTypeComponent(
	@Serializable(with = ResourceLocationArgument.Companion.ResourceLocationArgumentSimpleSerializer::class)
	var value: DamageTypeArgument,
) : Component() {
	companion object {
		@Suppress("UNCHECKED_CAST")
		data object DamageTypeComponentSerializer : InlineAutoSerializer<DamageTypeComponent, DamageTypeArgument>(
			ResourceLocationArgument.Companion.ResourceLocationArgumentSimpleSerializer as KSerializer<DamageTypeArgument>,
			DamageTypeComponent::value,
			::DamageTypeComponent,
		)
	}
}

/** Specifies the damage type dealt when attacking with this item. */
fun ComponentsScope.damageType(value: DamageTypeArgument) = apply {
	this[ItemComponentTypes.DAMAGE_TYPE] = DamageTypeComponent(value)
}

fun ComponentsScope.damageType(value: DamageTypes) = damageType(value as DamageTypeArgument)

fun ComponentsScope.damageType(value: String, namespace: String = "minecraft") = damageType(DamageTypeArgument(value, namespace))
