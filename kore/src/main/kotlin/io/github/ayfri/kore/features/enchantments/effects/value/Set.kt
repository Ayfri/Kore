package io.github.ayfri.kore.features.enchantments.effects.value

import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import kotlinx.serialization.Serializable

@Serializable
data class Set(
	var value: LevelBased = constantLevelBased(0),
) : ValueEffect()
