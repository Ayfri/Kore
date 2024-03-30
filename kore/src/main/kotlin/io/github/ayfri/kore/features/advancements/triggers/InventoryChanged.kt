package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.predicates.sub.ItemStack
import io.github.ayfri.kore.features.predicates.sub.Slots
import kotlinx.serialization.Serializable

@Serializable
data class InventoryChanged(
	var items: List<ItemStack>? = null,
	var slots: Slots? = null,
) : AdvancementTriggerCondition
