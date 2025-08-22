package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.DamageTypeOrTagArgument
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BlocksAttacks(
	@SerialName("block_delay_seconds")
	var blockDelaySeconds: Float = 0f,
	@SerialName("block_sound")
	var blockSound: SoundEventArgument? = null,
	@SerialName("damage_reductions")
	var damageReductions: List<DamageReduction>? = null,
	@SerialName("disable_cooldown_scale")
	var disableCooldownScale: Float = 1f,
	@SerialName("disable_sound")
	var disableSound: SoundEventArgument? = null,
	@SerialName("item_damage")
	var itemDamage: ItemDamage? = null,
) : Component()

@Serializable
data class DamageReduction(
	var base: Float,
	var factor: Float,
	var type: InlinableList<DamageTypeOrTagArgument>? = null,
)

@Serializable
data class ItemDamage(
	var base: Float,
	var factor: Float,
	var threshold: Float,
)

fun ComponentsScope.blocksAttacks(
	blockDelaySeconds: Float = 0f,
	blockSound: SoundEventArgument? = null,
	damageReductions: List<DamageReduction>? = null,
	disableCooldownScale: Float = 1f,
	disableSound: SoundEventArgument? = null,
	itemDamage: ItemDamage? = null,
	block: BlocksAttacks.() -> Unit = {},
) = apply {
	this[ItemComponentTypes.BLOCKS_ATTACKS] = BlocksAttacks(blockDelaySeconds, blockSound, damageReductions, disableCooldownScale, disableSound, itemDamage).apply(block)
}

fun BlocksAttacks.damageReduction(base: Float, factor: Float, type: List<DamageTypeOrTagArgument>? = null) = apply {
	damageReductions = (damageReductions ?: mutableListOf()) + DamageReduction(base, factor, type)
}

fun BlocksAttacks.damageReduction(base: Float, factor: Float, vararg type: DamageTypeOrTagArgument) = damageReduction(base, factor, type.toList())

fun BlocksAttacks.itemDamage(base: Float, factor: Float, threshold: Float) = apply {
	itemDamage = ItemDamage(base, factor, threshold)
}
