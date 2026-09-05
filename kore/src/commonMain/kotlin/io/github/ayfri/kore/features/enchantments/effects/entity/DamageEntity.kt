package io.github.ayfri.kore.features.enchantments.effects.entity

import io.github.ayfri.kore.features.enchantments.values.Constant
import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.LevelBasedScope
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import io.github.ayfri.kore.generated.arguments.types.DamageTypeArgument
import kotlinx.serialization.Serializable

/**
 * Deals [damageType] damage to the affected entity, rolled between [minDamage] and [maxDamage].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#damage_entity
 *
 * @property damageType The damage type the hit is attributed to.
 * @property minDamage The lowest damage dealt, in half-hearts.
 * @property maxDamage The highest damage dealt, in half-hearts.
 */
@Serializable
data class DamageEntity(
	var damageType: DamageTypeArgument,
	var minDamage: LevelBased = Constant(0f),
	var maxDamage: LevelBased = Constant(0f),
) : EntityEffect(), LevelBasedScope

/** Sets [DamageEntity.minDamage] to a constant [value] in half-hearts. */
fun DamageEntity.minDamage(value: Float) {
	minDamage = constantLevelBased(value)
}

/** Sets [DamageEntity.minDamage] to a constant [value] in half-hearts. */
fun DamageEntity.minDamage(value: Int) {
	minDamage = constantLevelBased(value)
}

/** Sets [DamageEntity.maxDamage] to a constant [value] in half-hearts. */
fun DamageEntity.maxDamage(value: Float) {
	maxDamage = constantLevelBased(value)
}

/** Sets [DamageEntity.maxDamage] to a constant [value] in half-hearts. */
fun DamageEntity.maxDamage(value: Int) {
	maxDamage = constantLevelBased(value)
}

/** Sets both [DamageEntity.minDamage] and [DamageEntity.maxDamage] to a constant [value] in half-hearts. */
fun DamageEntity.damage(value: Float) {
	minDamage = constantLevelBased(value)
	maxDamage = constantLevelBased(value)
}

/** Sets both [DamageEntity.minDamage] and [DamageEntity.maxDamage] to a constant [value] in half-hearts. */
fun DamageEntity.damage(value: Int) {
	minDamage = constantLevelBased(value)
	maxDamage = constantLevelBased(value)
}
