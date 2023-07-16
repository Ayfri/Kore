package features.advancements.triggers

import arguments.types.resources.RecipeArgument
import features.advancements.types.ItemStack
import kotlinx.serialization.Serializable

@Serializable
data class RecipeCrafted(
	var recipeId: RecipeArgument,
	var ingredients: List<ItemStack>? = null,
) : AdvancementTriggerCondition
