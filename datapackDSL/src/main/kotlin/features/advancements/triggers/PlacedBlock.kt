package features.advancements.triggers

import arguments.Argument
import features.advancements.states.State
import features.advancements.types.ItemStack
import features.advancements.types.Location
import kotlinx.serialization.Serializable

@Serializable
data class PlacedBlock(
	var block: Argument.Block? = null,
	var item: ItemStack? = null,
	var location: Location? = null,
	var state: Map<String, State<*>>? = null,
) : AdvancementTriggerCondition
