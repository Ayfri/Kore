package io.github.ayfri.kore.features.advancements.types

import io.github.ayfri.kore.arguments.types.resources.EffectArgument
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.buildNbtCompound
import kotlinx.serialization.Serializable

@Serializable
data class ItemStack(
	var count: IntRangeOrIntJson? = null,
	var durability: IntRangeOrIntJson? = null,
	var enchantments: List<Enchantment>? = null,
	var storedEnchantments: List<Enchantment>? = null,
	var items: List<ItemArgument>? = null,
	var nbt: NbtCompound? = null,
	var potion: EffectArgument? = null,
	var tag: String? = null,
)

fun itemStack(init: ItemStack.() -> Unit = {}) = ItemStack().apply(init)

fun itemStack(item: ItemArgument, init: ItemStack.() -> Unit = {}) = ItemStack(items = listOf(item)).apply(init)

fun ItemStack.item(vararg items: ItemArgument) {
	this.items = items.toList()
}

fun ItemStack.nbt(block: NbtCompoundBuilder.() -> Unit) {
	nbt = buildNbtCompound(block)
}
