package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.EnchantmentArgument
import io.github.ayfri.kore.serializers.SinglePropertySimplifierSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(with = Enchantments.Companion.EnchantmentsSerializer::class)
data class Enchantments(
	var levels: MutableMap<EnchantmentArgument, Int> = mutableMapOf(),
	@SerialName("show_in_tooltip")
	var showInTooltip: Boolean? = null,
) : Component() {
	companion object {
		data object EnchantmentsSerializer : SinglePropertySimplifierSerializer<Enchantments, Map<EnchantmentArgument, Int>>(
			Enchantments::class,
			Enchantments::levels,
		)
	}
}

fun ComponentsScope.enchantments(levels: Map<EnchantmentArgument, Int>, showInTooltip: Boolean? = null) =
	apply { this[ItemComponentTypes.ENCHANTMENTS] = Enchantments(levels.toMutableMap(), showInTooltip) }

fun ComponentsScope.enchantments(block: Enchantments.() -> Unit) = apply { this[ItemComponentTypes.ENCHANTMENTS] = Enchantments().apply(block) }

fun Enchantments.enchantment(enchantment: EnchantmentArgument, level: Int) = apply { levels[enchantment] = level }
