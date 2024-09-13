package io.github.ayfri.kore.features.enchantments.effects.entity

import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import kotlinx.serialization.Serializable

@Serializable
data class Ignite(
	var duration: LevelBased = constantLevelBased(0),
) : EntityEffect()

fun Ignite.duration(value: Int) {
	duration = constantLevelBased(value)
}