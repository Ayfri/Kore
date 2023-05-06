package data.item.builders

import arguments.Argument
import arguments.ChatComponents
import data.item.AttributeModifier
import data.item.Enchantment
import data.item.HideFlags
import data.item.ItemStack
import nbt.MutableNbtCompound
import net.benwoodworth.knbt.StringifiedNbt
import net.benwoodworth.knbt.encodeToNbtTag

data class ItemStackBuilder(var id: Argument.Item, var count: Short = 1) {
	var tag = MutableNbtCompound()

	var canPlaceOn: List<Argument.Block>? = null
	var canDestroy: List<Argument.Block>? = null
	var customModelData: Int? = null
	var enchantments: Set<Enchantment>? = null
	var hideFlags: Set<HideFlags>? = null
	var modifiers: Set<AttributeModifier>? = null
	var name: ChatComponents? = null

	fun build() = ItemStack(
		id = id.asId(),
		count = count,
		tag = tag.apply {
			canPlaceOn?.let { this["CanPlaceOn"] = StringifiedNbt.encodeToNbtTag(it.map(Argument.Block::asId)) }
			canDestroy?.let { this["CanDestroy"] = StringifiedNbt.encodeToNbtTag(it.map(Argument.Block::asId)) }
			customModelData?.let { this["CustomModelData"] = it }
			enchantments?.let { this["Enchantments"] = StringifiedNbt.encodeToNbtTag(it) }
			hideFlags?.let { this["HideFlags"] = it.map(HideFlags::toBitFlag).sum() }
			modifiers?.let { this["AttributeModifiers"] = StringifiedNbt.encodeToNbtTag(it) }
			name?.let { this["Name"] = StringifiedNbt.encodeToNbtTag(it) }
		}.toNbtCompound(),
	)
}

fun itemStack(item: Argument.Item, count: Short = 1, init: ItemStackBuilder.() -> Unit = {}) =
	ItemStackBuilder(item, count).apply(init).build()

fun ItemStackBuilder.canPlaceOn(vararg blocks: Argument.Block) {
	canPlaceOn = blocks.toList()
}

fun ItemStackBuilder.canDestroy(vararg blocks: Argument.Block) {
	canDestroy = blocks.toList()
}

fun ItemStackBuilder.enchantments(vararg enchantments: Enchantment) {
	this.enchantments = enchantments.toSet()
}

fun ItemStackBuilder.enchantments(block: EnchantmentsBuilder.() -> Unit) {
	enchantments = EnchantmentsBuilder().apply(block).enchantments
}

fun ItemStackBuilder.hideFlags(vararg hideFlags: HideFlags) {
	this.hideFlags = hideFlags.toSet()
}

fun ItemStackBuilder.modifiers(vararg modifiers: AttributeModifier) {
	this.modifiers = modifiers.toSet()
}

fun ItemStackBuilder.modifiers(block: AttributeModifiersBuilder.() -> Unit) {
	modifiers = AttributeModifiersBuilder().apply(block).modifiers
}