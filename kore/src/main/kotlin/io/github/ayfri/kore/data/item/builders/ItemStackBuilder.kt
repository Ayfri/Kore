package io.github.ayfri.kore.data.item.builders

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.data.item.AttributeModifier
import io.github.ayfri.kore.data.item.Enchantment
import io.github.ayfri.kore.data.item.HideFlags
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.nbt.MutableNbtCompound

data class ItemStackBuilder(var id: ItemArgument, var count: Short = 1) {
	var tag = MutableNbtCompound()

	var canPlaceOn: List<BlockArgument>? = null
	var canDestroy: List<BlockArgument>? = null
	var customModelData: Int? = null
	var enchantments: Set<Enchantment>? = null
	var hideFlags: Set<HideFlags>? = null
	var modifiers: Set<AttributeModifier>? = null
	var name: ChatComponents? = null

	fun build() = ItemStack(
		id = id.asId(),
		count = count,
		// TODO: To update.
		/* 		tag = tag.apply {
					canPlaceOn?.let { this["CanPlaceOn"] = StringifiedNbt.encodeToNbtTag(it.map(BlockArgument::asId)) }
					canDestroy?.let { this["CanDestroy"] = StringifiedNbt.encodeToNbtTag(it.map(BlockArgument::asId)) }
					customModelData?.let { this["CustomModelData"] = it }
					enchantments?.let { this["Enchantments"] = StringifiedNbt.encodeToNbtTag(it) }
					hideFlags?.let { this["HideFlags"] = it.map(HideFlags::toBitFlag).sum() }
					modifiers?.let { this["AttributeModifiers"] = StringifiedNbt.encodeToNbtTag(it) }
					name?.let { this["Name"] = StringifiedNbt.encodeToNbtTag(it) }
				}.toNbtCompound(), */
	)
}

fun itemStack(item: ItemArgument, count: Short = 1, init: ItemStackBuilder.() -> Unit = {}) =
	ItemStackBuilder(item, count).apply(init).build()

fun ItemStackBuilder.canPlaceOn(vararg blocks: BlockArgument) {
	canPlaceOn = blocks.toList()
}

fun ItemStackBuilder.canDestroy(vararg blocks: BlockArgument) {
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
