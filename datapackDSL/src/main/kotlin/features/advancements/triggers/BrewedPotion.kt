package features.advancements.triggers

import arguments.Argument
import kotlinx.serialization.Serializable

@Serializable
data class BrewedPotion(
	var potion: Argument.Potion? = null,
) : AdvancementTriggerCondition
