package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.types.resources.EnchantmentArgument
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StoredEnchantments(
	var levels: MutableMap<EnchantmentArgument, Int> = mutableMapOf(),
	@SerialName("show_in_tooltip")
	var showInTooltip: Boolean? = null,
) :
	Component()

fun Components.storedEnchantments(levels: Map<EnchantmentArgument, Int>, showInTooltip: Boolean? = null) =
	apply { components["stored_enchantments"] = StoredEnchantments(levels.toMutableMap(), showInTooltip) }

fun Components.storedEnchantments(block: StoredEnchantments.() -> Unit) =
	apply { components["stored_enchantments"] = StoredEnchantments().apply(block) }

fun StoredEnchantments.enchantment(enchantment: EnchantmentArgument, level: Int) = apply { levels[enchantment] = level }
