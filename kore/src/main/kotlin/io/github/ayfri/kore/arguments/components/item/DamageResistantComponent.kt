package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.tagged.DamageTypeTagArgument
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:damage_resistant` item component, which makes the item entity resistant to specific damage types (e.g., fire, explosions).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#damage_resistant
 */
@Serializable
data class DamageResistantComponent(
	var types: DamageTypeTagArgument,
) : Component()

/** Makes the item entity resistant to specific damage types (e.g., fire, explosions). */
fun ComponentsScope.damageResistant(types: DamageTypeTagArgument) = apply {
	this[ItemComponentTypes.DAMAGE_RESISTANT] = DamageResistantComponent(types)
}
