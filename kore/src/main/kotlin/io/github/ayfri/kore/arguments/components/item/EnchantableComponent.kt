package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:enchantable` item component, which defines the enchantability value affecting enchantment quality at enchanting tables.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#enchantable
 */
@Serializable
data class EnchantableComponent(
	var value: Int,
) : Component()

/** Defines the enchantability value affecting enchantment quality at enchanting tables. */
fun ComponentsScope.enchantable(value: Int) = apply { this[ItemComponentTypes.ENCHANTABLE] = EnchantableComponent(value) }
