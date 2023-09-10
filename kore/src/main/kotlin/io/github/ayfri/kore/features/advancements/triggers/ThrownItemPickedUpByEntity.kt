package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.advancements.types.ItemStack
import kotlinx.serialization.Serializable

@Serializable
data class ThrownItemPickedUpByEntity(
	var entity: EntityOrPredicates? = null,
	var item: ItemStack? = null,
) : AdvancementTriggerCondition
