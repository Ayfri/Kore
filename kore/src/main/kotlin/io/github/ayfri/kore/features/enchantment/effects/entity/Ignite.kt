package io.github.ayfri.kore.features.enchantment.effects.entity

import io.github.ayfri.kore.features.enchantment.values.LevelBased
import io.github.ayfri.kore.features.enchantment.values.constantLevelBased
import kotlinx.serialization.Serializable

@Serializable
data class Ignite(
	var duration: LevelBased = constantLevelBased(0),
) : EntityEffect()

fun Ignite.duration(value: Int) {
	duration = constantLevelBased(value)
}
