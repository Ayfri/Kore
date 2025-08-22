package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.EnchantmentArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = StoredEnchantments.Companion.StoredEnchantmentsSerializer::class)
data class StoredEnchantments(var levels: MutableMap<EnchantmentArgument, Int> = mutableMapOf()) : Component() {
	companion object {
		data object StoredEnchantmentsSerializer : InlineAutoSerializer<StoredEnchantments>(StoredEnchantments::class) {}
	}
}

fun ComponentsScope.storedEnchantments(levels: Map<EnchantmentArgument, Int>) = apply {
	this[ItemComponentTypes.STORED_ENCHANTMENTS] = StoredEnchantments(levels.toMutableMap())
}

fun ComponentsScope.storedEnchantments(block: StoredEnchantments.() -> Unit) = apply {
	this[ItemComponentTypes.STORED_ENCHANTMENTS] = StoredEnchantments().apply(block)
}

fun StoredEnchantments.enchantment(enchantment: EnchantmentArgument, level: Int) = apply { levels[enchantment] = level }
