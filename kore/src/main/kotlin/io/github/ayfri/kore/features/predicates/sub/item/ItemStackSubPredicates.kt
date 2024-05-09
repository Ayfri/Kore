package io.github.ayfri.kore.features.predicates.sub.item

import io.github.ayfri.kore.arguments.types.EffectOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.buildNbtCompound

@Serializable
data class ItemStackSubPredicates(
	@SerialName("minecraft:custom_data")
	var customData: NbtCompound? = null,
	@SerialName("minecraft:damage")
	var damage: Damage? = null,
	@SerialName("minecraft:potion_contents")
	var potionsContents: InlinableList<EffectOrTagArgument>? = null,
	@SerialName("minecraft:enchantments")
	var enchantments: List<Enchantment>? = null,
	@SerialName("minecraft:stored_enchantments")
	var storedEnchantments: List<Enchantment>? = null,
)

fun ItemStackSubPredicates.customData(block: NbtCompoundBuilder.() -> Unit) {
	customData = buildNbtCompound(block)
}

fun ItemStackSubPredicates.damage(block: Damage.() -> Unit) {
	damage = Damage().apply(block)
}

fun ItemStackSubPredicates.enchantments(vararg enchantments: Enchantment) {
	this.enchantments = enchantments.toList()
}

fun ItemStackSubPredicates.potionsContents(vararg potions: EffectOrTagArgument) {
	potionsContents = potions.toList()
}

fun ItemStackSubPredicates.storedEnchantments(vararg enchantments: Enchantment) {
	this.storedEnchantments = enchantments.toList()
}
