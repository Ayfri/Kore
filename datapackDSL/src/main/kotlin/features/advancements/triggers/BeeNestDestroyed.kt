package features.advancements.triggers

import arguments.types.resources.BlockArgument
import features.advancements.serializers.IntRangeOrIntJson
import features.advancements.types.ItemStack
import kotlinx.serialization.Serializable

@Serializable
data class BeeNestDestroyed(
	var block: BlockArgument? = null,
	var item: ItemStack? = null,
	var numBeesInside: IntRangeOrIntJson? = null
) : AdvancementTriggerCondition
