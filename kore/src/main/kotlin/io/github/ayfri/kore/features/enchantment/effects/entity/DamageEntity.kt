package io.github.ayfri.kore.features.enchantment.effects.entity

import io.github.ayfri.kore.arguments.types.resources.DamageTypeArgument
import io.github.ayfri.kore.features.enchantment.values.LevelBased
import io.github.ayfri.kore.features.enchantment.values.constantLevelBased
import kotlinx.serialization.Serializable

@Serializable
data class DamageEntity(
	var damageType: DamageTypeArgument,
	var minDamage: LevelBased = constantLevelBased(0),
	var maxDamage: LevelBased = constantLevelBased(0),
) : EntityEffect()


fun DamageEntity.minDamage(value: Int) {
	minDamage = constantLevelBased(value)
}

fun DamageEntity.maxDamage(value: Int) {
	maxDamage = constantLevelBased(value)
}
