package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.EnchantmentArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = Enchantments.Companion.EnchantmentsSerializer::class)
data class Enchantments(var levels: MutableMap<EnchantmentArgument, Int> = mutableMapOf()) : Component() {
	companion object {
		data object EnchantmentsSerializer : InlineAutoSerializer<Enchantments>(Enchantments::class)
	}
}

fun ComponentsScope.enchantments(levels: Map<EnchantmentArgument, Int>) = apply {
	this[ItemComponentTypes.ENCHANTMENTS] = Enchantments(levels.toMutableMap())
}

fun ComponentsScope.enchantments(block: Enchantments.() -> Unit) = apply {
	this[ItemComponentTypes.ENCHANTMENTS] = Enchantments().apply(block)
}

fun Enchantments.enchantment(enchantment: EnchantmentArgument, level: Int) = apply { levels[enchantment] = level }
