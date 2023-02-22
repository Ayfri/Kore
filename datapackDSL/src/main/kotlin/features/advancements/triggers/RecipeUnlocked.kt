package features.advancements.triggers

import arguments.Argument
import kotlinx.serialization.Serializable

@Serializable
data class RecipeUnlocked(
	var recipe: Argument.Recipe,
) : AdvancementTriggerCondition
