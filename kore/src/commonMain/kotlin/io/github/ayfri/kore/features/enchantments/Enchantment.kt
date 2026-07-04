package io.github.ayfri.kore.features.enchantments

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.data.EquipmentSlot
import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.features.enchantments.values.Linear
import io.github.ayfri.kore.generated.arguments.EnchantmentOrTagArgument
import io.github.ayfri.kore.generated.arguments.types.EnchantmentArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven definition for a custom enchantment.
 *
 * Allows you to define enchantments with full control over supported items, enchanting table
 * appearance, power cost formulas, anvil costs, exclusivity (mutually incompatible sets),
 * applicable equipment slots, and effect components.
 *
 * JSON format reference: https://minecraft.wiki/w/Enchantment_definition
 * Docs: https://kore.ayfri.com/docs/data-driven/enchantments
 *
 * @param description - The text component used as a description in tooltips.
 * @param exclusiveSet - The list of enchantments that are mutually exclusive with this enchantment.
 * @param supportedItems - The list of items that can receive this enchantment in any context.
 * @param primaryItems - The list of items that are preferred in enchanting tables and villager trades.
 * @param weight - The weight of the enchantment in the enchantment table.
 * @param maxLevel - The maximum level of the enchantment.
 * @param minCost - The base and per-level formula for the minimum enchanting power.
 * @param maxCost - The base and per-level formula for the maximum enchanting power.
 * @param anvilCost - The cost of the enchantment in an anvil.
 * @param slots - The valid equipment slots for this enchantment's effects.
 * @param effects - The effect components for this enchantment.
 */
@Serializable
data class Enchantment(
	@Transient
	override var fileName: String = "enchantment",
	var description: ChatComponents = textComponent(),
	var exclusiveSet: InlinableList<EnchantmentOrTagArgument>? = null,
	var supportedItems: InlinableList<ItemArgument> = emptyList(),
	var primaryItems: InlinableList<ItemOrTagArgument>? = null,
	var weight: Int = 1,
	var maxLevel: Int = 1,
	var minCost: Linear = Linear(0, 0),
	var maxCost: Linear = Linear(0, 0),
	var anvilCost: Int = 0,
	var slots: List<EquipmentSlot> = emptyList(),
	var effects: EnchantmentEffects? = null,
) : Generator("enchantment") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Register a new enchantment file in the datapack.
 *
 * Configure supported items, primary items, min/max enchanting cost formulas, equipment slots,
 * and effect blocks inside [init].
 *
 * Produces `data/<namespace>/enchantment/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/enchantments
 */
fun DataPack.enchantment(fileName: String, init: Enchantment.() -> Unit): EnchantmentArgument {
	val enchantment = Enchantment(fileName).apply(init)
	enchantments += enchantment
	return EnchantmentArgument(fileName, enchantment.namespace ?: name)
}

/**
 * Set the translatable description shown on the enchantment tooltip.
 */
fun Enchantment.description(text: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) {
	description = textComponent(text, color, block)
}

/** Limit this enchantment to be mutually exclusive with the given list. */
fun Enchantment.exclusiveSet(vararg enchantments: EnchantmentOrTagArgument) {
	exclusiveSet = enchantments.toList()
}

/** Set the items that can receive this enchantment in any context. */
fun Enchantment.supportedItems(vararg items: ItemArgument) {
	supportedItems = items.toList()
}

/** Prefer these items in enchanting tables and villager trades. */
fun Enchantment.primaryItems(vararg items: ItemArgument) {
	primaryItems = items.toList()
}

/** Prefer these items in enchanting tables and villager trades. */
fun Enchantment.primaryItems(item: ItemOrTagArgument) {
	primaryItems = listOf(item)
}

/** Define base and per-level formula for the minimum enchanting power. */
fun Enchantment.minCost(base: Int, perLevelAboveFirst: Int) {
	minCost = Linear(base, perLevelAboveFirst)
}

/** Define base and per-level formula for the maximum enchanting power. */
fun Enchantment.maxCost(base: Int, perLevelAboveFirst: Int) {
	maxCost = Linear(base, perLevelAboveFirst)
}

/** Restrict valid equipment slots for this enchantment's effects. */
fun Enchantment.slots(vararg slots: EquipmentSlot) {
	this.slots = slots.toList()
}

/** Configure additional effect blocks (attributes, damage, etc.). */
fun Enchantment.effects(block: EnchantmentEffects.() -> Unit) {
	effects = EnchantmentEffects().apply(block)
}
