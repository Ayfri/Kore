package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.DamageTypeOrTagArgument
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:blocks_attacks` item component, which allows the item to block incoming attacks like a shield.
 *
 * @property blockDelaySeconds Time in seconds the item must be held before blocking activates. Defaults to `0`.
 * @property blockSound Sound played when an attack is successfully blocked. Defaults to none.
 * @property bypassedBy Damage type(s) that bypass this item's blocking entirely. Defaults to none.
 * @property damageReductions Rules controlling how much damage is absorbed per attack. Defaults to blocking all damage within 90 degrees.
 * @property disableCooldownScale Multiplier applied to the disable cooldown when a disabling attack lands. `0` means the item can never be disabled. Defaults to `1`.
 * @property disableSound Sound played when the item is disabled by an attack. Defaults to none.
 * @property itemDamage Controls how much durability the item loses from a blocked hit. Defaults to none.
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
	var bypassedBy: InlinableList<DamageTypeOrTagArgument>? = null,
	@SerialName("damage_reductions")
	var damageReductions: List<DamageReduction>? = null,
	@SerialName("disable_cooldown_scale")
	var disableCooldownScale: Float = 1f,
	@SerialName("disable_sound")
	var disableSound: SoundEventArgument? = null,
	@SerialName("item_damage")
	var itemDamage: ItemDamage? = null,
) : Component()

/**
 * A single damage reduction rule applied while blocking.
 *
 * @property base Constant amount of damage blocked, regardless of how much was dealt.
 * @property factor Fraction of the dealt damage that is blocked.
 * @property horizontalBlockingAngle Max angle (degrees) between the user's facing direction and the attack direction for the block to apply. Defaults to `90`.
 * @property type Damage type(s) this rule applies to. Defaults to all types.
 */
@Serializable
data class DamageReduction(
	var base: Float,
	var factor: Float,
	@SerialName("horizontal_blocking_angle")
	var horizontalBlockingAngle: Float? = null,
	var type: InlinableList<DamageTypeOrTagArgument>? = null,
)

/**
 * Controls how much durability the blocking item loses from a hit.
 *
 * @property base Constant durability damage applied once [threshold] is exceeded.
 * @property factor Fraction of the dealt damage applied as durability damage once [threshold] is exceeded.
 * @property threshold Minimum incoming damage before any durability loss is applied.
 */
@Serializable
data class ItemDamage(
	var base: Float,
	var factor: Float,
	var threshold: Float,
)

/** Adds the `minecraft:blocks_attacks` component, allowing this item to block incoming attacks like a shield. */
fun ComponentsScope.blocksAttacks(block: BlocksAttacks.() -> Unit = {}) = apply {
	this[ItemComponentTypes.BLOCKS_ATTACKS] = BlocksAttacks().apply(block)
}

/** Sets the damage type(s) that bypass this item's blocking. Replaces any previously set value. */
fun BlocksAttacks.bypassedBy(vararg types: DamageTypeOrTagArgument) {
	bypassedBy = types.toList()
}

/**
 * Appends a damage reduction rule to [damageReductions].
 *
 * @param base Constant damage blocked per hit.
 * @param factor Fraction of dealt damage blocked.
 * @param horizontalBlockingAngle Max angle for the block to apply. Defaults to `90`.
 * @param type Damage types this rule applies to. `null` means all types.
 */
fun BlocksAttacks.damageReduction(
	base: Float,
	factor: Float,
	horizontalBlockingAngle: Float? = null,
	type: List<DamageTypeOrTagArgument>? = null,
) = apply {
	damageReductions = (damageReductions ?: mutableListOf()) + DamageReduction(base, factor, horizontalBlockingAngle, type = type)
}

/** Appends a damage reduction rule to [damageReductions] using vararg [type] values. */
fun BlocksAttacks.damageReduction(
	base: Float,
	factor: Float,
	horizontalBlockingAngle: Float? = null,
	vararg type: DamageTypeOrTagArgument,
) = damageReduction(base, factor, horizontalBlockingAngle, type.toList())

/** Sets the [ItemDamage] rule controlling how much durability this item loses per blocked hit. */
fun BlocksAttacks.itemDamage(base: Float, factor: Float, threshold: Float) = apply {
	itemDamage = ItemDamage(base, factor, threshold)
}
