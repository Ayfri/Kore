package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.types.EffectOrTagArgument
import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.serializers.InlinableList
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
	var items: InlinableList<ItemOrTagArgument>? = null,
	var components: Components? = null,
	var customData: NbtCompound? = null,
	var potions: InlinableList<EffectOrTagArgument>? = null,
	var tag: String? = null,
)

fun itemStack(init: ItemStack.() -> Unit = {}) = ItemStack().apply(init)

fun itemStack(item: ItemArgument, init: ItemStack.() -> Unit = {}) = ItemStack(items = listOf(item)).apply(init)

fun ItemStack.item(vararg items: ItemArgument) {
	this.items = items.toList()
}

fun ItemStack.components(block: Components.() -> Unit) {
	components = Components().apply(block)
}

fun ItemStack.customData(block: NbtCompoundBuilder.() -> Unit) {
	customData = buildNbtCompound(block)
}

fun ItemStack.enchantments(vararg enchantments: Enchantment) {
	this.enchantments = enchantments.toList()
}

fun ItemStack.potions(vararg potions: EffectOrTagArgument) {
	this.potions = potions.toList()
}

fun ItemStack.storedEnchantments(vararg enchantments: Enchantment) {
	this.storedEnchantments = enchantments.toList()
}
