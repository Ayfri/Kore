package features.advancements.triggers

import arguments.Argument
import features.advancements.types.ItemStack
import kotlinx.serialization.Serializable

@Serializable
data class RecipeCrafted(
	var recipeId: Argument.Recipe,
	var ingredients: List<ItemStack>? = null,
) : AdvancementTriggerCondition
