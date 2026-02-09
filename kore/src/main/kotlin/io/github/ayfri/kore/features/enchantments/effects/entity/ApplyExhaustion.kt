package io.github.ayfri.kore.features.enchantments.effects.entity

import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import kotlinx.serialization.Serializable

@Serializable
data class ApplyExhaustion(
	var amount: LevelBased = constantLevelBased(0),
) : EntityEffect()

fun ApplyExhaustion.amount(value: Int) {
	amount = constantLevelBased(value)
}
