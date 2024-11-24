package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.resources.tagged.DamageTypeTagArgument
import io.github.ayfri.kore.generated.ComponentTypes
import kotlinx.serialization.Serializable

@Serializable
data class DamageResistantComponent(
	var types: DamageTypeTagArgument,
) : Component()

fun ComponentsScope.damageResistant(types: DamageTypeTagArgument) = apply {
	this[ComponentTypes.DAMAGE_RESISTANT] = DamageResistantComponent(types)
}
