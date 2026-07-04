package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.DamageTypeOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:damage_resistant` item component, which makes the item entity invulnerable to the specified damage types when in entity form or equipped.
 *
 * @property types Damage type(s) the item is immune to. Accepts a tag, a single ID, or a list of IDs/tags.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#damage_resistant
 */
@Serializable
data class DamageResistantComponent(
	var types: InlinableList<DamageTypeOrTagArgument>,
) : Component()

/**
 * Adds the `minecraft:damage_resistant` component, making this item immune to the given damage [types] when in entity form or equipped.
 *
 * @param types Damage type(s) the item should be resistant to.
 */
fun ComponentsScope.damageResistant(types: InlinableList<DamageTypeOrTagArgument>) = apply {
	this[ItemComponentTypes.DAMAGE_RESISTANT] = DamageResistantComponent(types)
}

/** Adds the `minecraft:damage_resistant` component using vararg [types]. */
fun ComponentsScope.damageResistant(vararg types: DamageTypeOrTagArgument) = damageResistant(types.toList())
