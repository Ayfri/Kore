package features.advancements.types

import arguments.Argument
import features.advancements.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.buildNbtCompound

@Serializable
data class ItemStack(
	var count: IntRangeOrIntJson? = null,
	var durability: IntRangeOrIntJson? = null,
	var enchantments: List<Enchantment>? = null,
	var storedEnchantments: List<Enchantment>? = null,
	var items: List<Argument.Item>? = null,
	var nbt: NbtCompound? = null,
	var potion: Argument.MobEffect? = null,
	var tag: String? = null,
)

fun itemStack(init: ItemStack.() -> Unit = {}): ItemStack {
	val itemStack = ItemStack()
	itemStack.init()
	return itemStack
}

fun itemStack(item: Argument.Item, init: ItemStack.() -> Unit = {}): ItemStack {
	val itemStack = ItemStack(items = listOf(item))
	itemStack.init()
	return itemStack
}

fun ItemStack.item(vararg items: Argument.Item) {
	this.items = items.toList()
}

fun ItemStack.nbt(block: NbtCompoundBuilder.() -> Unit) {
	nbt = buildNbtCompound(block)
}
