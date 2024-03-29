package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.types.resources.EnchantmentArgument
import io.github.ayfri.kore.generated.ComponentTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Enchantments(
	var levels: MutableMap<EnchantmentArgument, Int> = mutableMapOf(),
	@SerialName("show_in_tooltip")
	var showInTooltip: Boolean? = null,
) : Component()

fun Components.enchantments(levels: Map<EnchantmentArgument, Int>, showInTooltip: Boolean? = null) =
	apply { this[ComponentTypes.ENCHANTMENTS] = Enchantments(levels.toMutableMap(), showInTooltip) }

fun Components.enchantments(block: Enchantments.() -> Unit) = apply { this[ComponentTypes.ENCHANTMENTS] = Enchantments().apply(block) }

fun Enchantments.enchantment(enchantment: EnchantmentArgument, level: Int) = apply { levels[enchantment] = level }
