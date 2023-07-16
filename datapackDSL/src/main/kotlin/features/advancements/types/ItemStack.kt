package features.advancements.types

import arguments.types.resources.ItemArgument
import arguments.types.resources.MobEffectArgument
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
	var items: List<ItemArgument>? = null,
	var nbt: NbtCompound? = null,
	var potion: MobEffectArgument? = null,
	var tag: String? = null,
)

fun itemStack(init: ItemStack.() -> Unit = {}): ItemStack {
	val itemStack = ItemStack()
	itemStack.init()
	return itemStack
}

fun itemStack(item: ItemArgument, init: ItemStack.() -> Unit = {}): ItemStack {
	val itemStack = ItemStack(items = listOf(item))
	itemStack.init()
	return itemStack
}

fun ItemStack.item(vararg items: ItemArgument) {
	this.items = items.toList()
}

fun ItemStack.nbt(block: NbtCompoundBuilder.() -> Unit) {
	nbt = buildNbtCompound(block)
}
