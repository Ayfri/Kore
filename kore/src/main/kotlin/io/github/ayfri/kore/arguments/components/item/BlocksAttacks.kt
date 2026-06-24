package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.DamageTypeOrTagArgument
import io.github.ayfri.kore.generated.arguments.tagged.DamageTypeTagArgument
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:blocks_attacks` item component, which configures how the item blocks incoming attacks when used (like a shield).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#blocks_attacks
 */
@Serializable
data class BlocksAttacks(
	@SerialName("block_delay_seconds")
	var blockDelaySeconds: Float = 0f,
	@SerialName("block_sound")
	var blockSound: SoundEventArgument? = null,
	@SerialName("bypassed_by")
	var bypassedBy: DamageTypeTagArgument? = null,
	@SerialName("damage_reductions")
	var damageReductions: List<DamageReduction>? = null,
	@SerialName("disable_cooldown_scale")
	var disableCooldownScale: Float = 1f,
	@SerialName("disable_sound")
	var disableSound: SoundEventArgument? = null,
	@SerialName("item_damage")
	var itemDamage: ItemDamage? = null,
) : Component()

/** A damage reduction rule applied while blocking, optionally limited to certain damage [type]s and blocking angle. */
@Serializable
data class DamageReduction(
	var base: Float,
	var factor: Float,
	@SerialName("horizontal_blocking_angle")
	var horizontalBlockingAngle: Float? = null,
	var type: InlinableList<DamageTypeOrTagArgument>? = null,
)

/** How much durability the blocking item loses based on the damage taken. */
@Serializable
data class ItemDamage(
	var base: Float,
	var factor: Float,
	var threshold: Float,
)

/** Configures how the item blocks incoming attacks when used (like a shield). */
fun ComponentsScope.blocksAttacks(block: BlocksAttacks.() -> Unit = {}) = apply {
	this[ItemComponentTypes.BLOCKS_ATTACKS] = BlocksAttacks().apply(block)
}

fun BlocksAttacks.damageReduction(
	base: Float,
	factor: Float,
	horizontalBlockingAngle: Float? = null,
	type: List<DamageTypeOrTagArgument>? = null,
) = apply {
	damageReductions = (damageReductions ?: mutableListOf()) + DamageReduction(base, factor, horizontalBlockingAngle, type = type)
}

fun BlocksAttacks.damageReduction(
	base: Float,
	factor: Float,
	horizontalBlockingAngle: Float? = null,
	vararg type: DamageTypeOrTagArgument,
) = damageReduction(base, factor, horizontalBlockingAngle, type.toList())

fun BlocksAttacks.itemDamage(base: Float, factor: Float, threshold: Float) = apply {
	itemDamage = ItemDamage(base, factor, threshold)
}
