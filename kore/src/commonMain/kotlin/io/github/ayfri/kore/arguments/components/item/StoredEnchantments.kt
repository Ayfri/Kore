package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.EnchantmentArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Represents the `minecraft:stored_enchantments` item component, which embeds enchantments into an enchanted book without applying them to the holder.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#stored_enchantments
 */
@Serializable(with = StoredEnchantments.Companion.StoredEnchantmentsSerializer::class)
data class StoredEnchantments(var levels: MutableMap<EnchantmentArgument, Int> = mutableMapOf()) : Component() {
	companion object {
		data object StoredEnchantmentsSerializer :
			InlineAutoSerializer<StoredEnchantments, MutableMap<EnchantmentArgument, Int>>(
				serializer<MutableMap<EnchantmentArgument, Int>>(),
				StoredEnchantments::levels,
				::StoredEnchantments
			)
	}
}

/** Embeds enchantments into an enchanted book without applying them to the holder. */
fun ComponentsScope.storedEnchantments(levels: Map<EnchantmentArgument, Int>) = apply {
	this[ItemComponentTypes.STORED_ENCHANTMENTS] = StoredEnchantments(levels.toMutableMap())
}

fun ComponentsScope.storedEnchantments(block: StoredEnchantments.() -> Unit) = apply {
	this[ItemComponentTypes.STORED_ENCHANTMENTS] = StoredEnchantments().apply(block)
}

fun StoredEnchantments.enchantment(enchantment: EnchantmentArgument, level: Int) = apply { levels[enchantment] = level }
