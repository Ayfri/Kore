package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ComponentTypes
import io.github.ayfri.kore.generated.arguments.tagged.DamageTypeTagArgument
import kotlinx.serialization.Serializable

@Serializable
data class DamageResistantComponent(
	var types: DamageTypeTagArgument,
) : Component()

fun ComponentsScope.damageResistant(types: DamageTypeTagArgument) = apply {
	this[ComponentTypes.DAMAGE_RESISTANT] = DamageResistantComponent(types)
}
