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

fun DataPack.enchantment(fileName: String, init: Enchantment.() -> Unit): EnchantmentArgument {
	val enchantment = Enchantment(fileName).apply(init)
	enchantments += enchantment
	return EnchantmentArgument(fileName, enchantment.namespace ?: name)
}

fun Enchantment.description(text: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) {
	description = textComponent(text, color, block)
}

fun Enchantment.exclusiveSet(vararg enchantments: EnchantmentOrTagArgument) {
	exclusiveSet = enchantments.toList()
}

fun Enchantment.supportedItems(vararg items: ItemArgument) {
	supportedItems = items.toList()
}

fun Enchantment.primaryItems(vararg items: ItemArgument) {
	primaryItems = items.toList()
}

fun Enchantment.primaryItems(item: ItemOrTagArgument) {
	primaryItems = listOf(item)
}

fun Enchantment.minCost(base: Int, perLevelAboveFirst: Int) {
	minCost = Linear(base, perLevelAboveFirst)
}

fun Enchantment.maxCost(base: Int, perLevelAboveFirst: Int) {
	maxCost = Linear(base, perLevelAboveFirst)
}

fun Enchantment.slots(vararg slots: EquipmentSlot) {
	this.slots = slots.toList()
}

fun Enchantment.effects(block: EnchantmentEffects.() -> Unit) {
	effects = EnchantmentEffects().apply(block)
}
