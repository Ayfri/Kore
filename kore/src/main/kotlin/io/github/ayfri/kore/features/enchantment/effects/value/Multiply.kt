package io.github.ayfri.kore.features.enchantment.effects.value

import io.github.ayfri.kore.features.enchantment.values.LevelBased
import io.github.ayfri.kore.features.enchantment.values.constantLevelBased
import kotlinx.serialization.Serializable

@Serializable
data class Multiply(
	var factor: LevelBased = constantLevelBased(0),
) : ValueEffect()
