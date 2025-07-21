package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.Serializable

@Serializable
data class EnchantableComponent(
	var value: Int,
) : Component()

fun ComponentsScope.enchantable(value: Int) = apply { this[ItemComponentTypes.ENCHANTABLE] = EnchantableComponent(value) }
