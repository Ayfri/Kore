package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.types.ItemStack
import io.github.ayfri.kore.features.advancements.types.Slots
import kotlinx.serialization.Serializable

@Serializable
data class InventoryChanged(
	var items: List<ItemStack>? = null,
	var slots: Slots? = null,
) : AdvancementTriggerCondition
