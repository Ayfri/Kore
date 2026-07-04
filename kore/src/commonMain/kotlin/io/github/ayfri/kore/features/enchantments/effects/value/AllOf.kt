package io.github.ayfri.kore.features.enchantments.effects.value

import kotlinx.serialization.Serializable

@Serializable
data class AllOf(
	var effects: List<ValueEffect> = emptyList(),
) : ValueEffect()
