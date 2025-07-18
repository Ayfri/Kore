package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument
import kotlinx.serialization.Serializable

@Serializable
data class RecipeCrafted(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var recipeId: RecipeArgument,
	var ingredients: List<ItemStack>? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.recipeCrafted(name: String, recipeId: RecipeArgument, block: RecipeCrafted.() -> Unit) {
	criteria += RecipeCrafted(name, recipeId = recipeId).apply(block)
}

fun RecipeCrafted.ingredients(block: ItemStack.() -> Unit) {
	ingredients = listOf(ItemStack().apply(block))
}

fun RecipeCrafted.ingredient(block: ItemStack.() -> Unit) {
	ingredients = (ingredients ?: emptyList()) + listOf(ItemStack().apply(block))
}
