package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.arguments.types.resources.RecipeArgument
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import kotlinx.serialization.Serializable

@Serializable
data class RecipeCrafted(
	var recipeId: RecipeArgument,
	var ingredients: List<ItemStack>? = null,
) : AdvancementTriggerCondition
