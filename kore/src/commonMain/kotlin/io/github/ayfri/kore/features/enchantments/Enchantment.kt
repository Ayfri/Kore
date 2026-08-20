package io.github.ayfri.kore.features.enchantments

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.data.EquipmentSlot
import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.generated.arguments.EnchantmentOrTagArgument
import io.github.ayfri.kore.generated.arguments.types.EnchantmentArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven definition for a custom enchantment.
 *
 * Defines which items the enchantment applies to, how it shows up in the enchanting table, what it costs on an
 * anvil, which enchantments it is incompatible with, the equipment slots its effects run in, and the effect
 * components themselves.
 *
 * Produces `data/<namespace>/enchantment/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/enchantments
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition
 *
 * @property description The text component shown as the enchantment name in tooltips.
 * @property exclusiveSet The enchantments that cannot be combined with this one, none when `null`.
 * @property supportedItems The items the enchantment can be applied to on an anvil or with `/enchant`.
 * @property primaryItems The subset of [supportedItems] the enchanting table offers it for, [supportedItems] when `null`.
 * @property weight How likely the enchantment is to be picked, from `1` to `1024`.
 * @property maxLevel The highest level the enchantment can reach, from `1` to `255`.
 * @property minCost The lowest enchanting power the enchantment can be offered at.
 * @property maxCost The highest enchanting power the enchantment can be offered at.
 * @property anvilCost The base level cost of applying the enchantment on an anvil.
 * @property slots The equipment slots the effects of the enchantment are active in.
 * @property effects The effect components driving what the enchantment actually does, none when `null`.
 */
@Serializable
data class Enchantment(
	@Transient
	override var fileName: String = "enchantment",
	var description: ChatComponents = textComponent(),
	var exclusiveSet: InlinableList<EnchantmentOrTagArgument>? = null,
	var supportedItems: InlinableList<ItemOrTagArgument> = emptyList(),
	var primaryItems: InlinableList<ItemOrTagArgument>? = null,
	var weight: Int = 1,
	var maxLevel: Int = 1,
	var minCost: EnchantmentCost = EnchantmentCost(),
	var maxCost: EnchantmentCost = EnchantmentCost(),
	var anvilCost: Int = 0,
	var slots: List<EquipmentSlot> = emptyList(),
	var effects: EnchantmentEffects? = null,
) : Generator("enchantment") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Registers a new enchantment in this datapack.
 *
 * Produces `data/<namespace>/enchantment/<fileName>.json`.
 *
 * ```kotlin
 * enchantment("frost_aspect") {
 *     description("Frost Aspect")
 *     supportedItems(Tags.Item.SWORDS)
 *     slots(EquipmentSlot.MAINHAND)
 *     maxLevel = 2
 *     minCost(10, 20)
 *     maxCost(60, 20)
 *
 *     effects {
 *         postAttack {
 *             on(PostAttackSpecifier.ATTACKER, PostAttackSpecifier.VICTIM) {
 *                 applyMobEffect(Effects.SLOWNESS) { maxDuration(100) }
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/enchantments
 */
fun DataPack.enchantment(fileName: String, init: Enchantment.() -> Unit): EnchantmentArgument {
	val enchantment = Enchantment(fileName).apply(init)
	enchantments += enchantment
	return EnchantmentArgument(fileName, enchantment.namespace ?: name)
}

/** Sets [Enchantment.description] to a plain text component, the name shown in tooltips. */
fun Enchantment.description(text: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) {
	description = textComponent(text, color, block)
}

/** Sets [Enchantment.exclusiveSet], the enchantments and enchantment tags this one cannot be combined with. */
fun Enchantment.exclusiveSet(vararg enchantments: EnchantmentOrTagArgument) {
	exclusiveSet = enchantments.toList()
}

/** Sets [Enchantment.supportedItems], the items and item tags the enchantment can be applied to. */
fun Enchantment.supportedItems(vararg items: ItemOrTagArgument) {
	supportedItems = items.toList()
}

/** Sets [Enchantment.primaryItems], the items and item tags the enchanting table offers the enchantment for. */
fun Enchantment.primaryItems(vararg items: ItemOrTagArgument) {
	primaryItems = items.toList()
}

/** Sets [Enchantment.minCost], the lowest enchanting power the enchantment can be offered at. */
fun Enchantment.minCost(base: Int, perLevelAboveFirst: Int) {
	minCost = EnchantmentCost(base, perLevelAboveFirst)
}

/** Sets [Enchantment.maxCost], the highest enchanting power the enchantment can be offered at. */
fun Enchantment.maxCost(base: Int, perLevelAboveFirst: Int) {
	maxCost = EnchantmentCost(base, perLevelAboveFirst)
}

/** Sets [Enchantment.slots], the equipment slots the effects of the enchantment are active in. */
fun Enchantment.slots(vararg slots: EquipmentSlot) {
	this.slots = slots.toList()
}

/** Sets [Enchantment.effects], the effect components driving what the enchantment does. */
fun Enchantment.effects(block: EnchantmentEffects.() -> Unit) {
	effects = EnchantmentEffects().apply(block)
}
