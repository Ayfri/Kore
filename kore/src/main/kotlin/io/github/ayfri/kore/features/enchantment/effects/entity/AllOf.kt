package io.github.ayfri.kore.features.enchantment.effects.entity

import kotlinx.serialization.Serializable

@Serializable
data class AllOf(
	var effects: List<EntityEffect> = emptyList(),
) : EntityEffect()
