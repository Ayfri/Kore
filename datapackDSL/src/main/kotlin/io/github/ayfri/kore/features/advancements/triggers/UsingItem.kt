package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.types.ItemStack
import kotlinx.serialization.Serializable

@Serializable
data class UsingItem(
	var item: ItemStack? = null,
) : AdvancementTriggerCondition
