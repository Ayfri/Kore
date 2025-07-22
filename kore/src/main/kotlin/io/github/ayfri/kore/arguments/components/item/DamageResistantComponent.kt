package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.tagged.DamageTypeTagArgument
import kotlinx.serialization.Serializable

@Serializable
data class DamageResistantComponent(
	var types: DamageTypeTagArgument,
) : Component()

fun ComponentsScope.damageResistant(types: DamageTypeTagArgument) = apply {
	this[ItemComponentTypes.DAMAGE_RESISTANT] = DamageResistantComponent(types)
}
