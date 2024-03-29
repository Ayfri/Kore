package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.types.resources.EnchantmentArgument
import io.github.ayfri.kore.generated.ComponentTypes
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
	apply { this[ComponentTypes.STORED_ENCHANTMENTS] = StoredEnchantments(levels.toMutableMap(), showInTooltip) }

fun Components.storedEnchantments(block: StoredEnchantments.() -> Unit) =
	apply { this[ComponentTypes.STORED_ENCHANTMENTS] = StoredEnchantments().apply(block) }

fun StoredEnchantments.enchantment(enchantment: EnchantmentArgument, level: Int) = apply { levels[enchantment] = level }
