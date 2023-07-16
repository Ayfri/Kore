package features.advancements.triggers

import arguments.types.resources.PotionArgument
import kotlinx.serialization.Serializable

@Serializable
data class BrewedPotion(
	var potion: PotionArgument? = null,
) : AdvancementTriggerCondition
