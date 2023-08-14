package features.advancements

import arguments.types.resources.FunctionArgument
import arguments.types.resources.LootTableArgument
import arguments.types.resources.RecipeArgument
import kotlinx.serialization.Serializable

@Serializable
data class AdvancementReward(
	var experience: Int? = null,
	var function: FunctionArgument? = null,
	var loot: List<LootTableArgument>? = null,
	var recipes: List<RecipeArgument>? = null,
)
