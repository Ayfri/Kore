package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import kotlinx.serialization.Serializable

@Serializable
data class BeeNestDestroyed(
	var block: BlockArgument? = null,
	var item: ItemStack? = null,
	var numBeesInside: IntRangeOrIntJson? = null,
) : AdvancementTriggerCondition
