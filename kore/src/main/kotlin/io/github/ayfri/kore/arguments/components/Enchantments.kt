package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.types.resources.EnchantmentArgument
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Enchantments(
	var levels: MutableMap<EnchantmentArgument, Int> = mutableMapOf(),
	@SerialName("show_in_tooltip")
	var showInTooltip: Boolean? = null,
) : Component()

fun Components.enchantments(levels: Map<EnchantmentArgument, Int>, showInTooltip: Boolean? = null) =
	apply { components["enchantments"] = Enchantments(levels.toMutableMap(), showInTooltip) }

fun Components.enchantments(block: Enchantments.() -> Unit) = apply { components["enchantments"] = Enchantments().apply(block) }

fun Enchantments.enchantment(enchantment: EnchantmentArgument, level: Int) = apply { levels[enchantment] = level }
