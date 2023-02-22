package features.advancements.triggers

import arguments.Argument
import features.advancements.serializers.IntRangeOrIntJson
import features.advancements.types.ItemStack
import kotlinx.serialization.Serializable

@Serializable
data class BeeNestDestroyed(
	var block: Argument.Block? = null,
	var item: ItemStack? = null,
	var numBeesInside: IntRangeOrIntJson? = null
) : AdvancementTriggerCondition
