package features.advancements.triggers

import arguments.types.resources.RecipeArgument
import kotlinx.serialization.Serializable

@Serializable
data class RecipeUnlocked(
	var recipe: RecipeArgument,
) : AdvancementTriggerCondition
