package io.github.ayfri.kore.features.enchantments.effects.entity

import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import io.github.ayfri.kore.generated.arguments.types.DamageTypeArgument
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
