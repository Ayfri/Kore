package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.arguments.types.resources.RecipeArgument
import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import kotlinx.serialization.Serializable

@Serializable
data class CrafterRecipeCrafted(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var ingredients: List<ItemStack>? = null,
	var recipeId: RecipeArgument,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.crafterRecipeCrafted(name: String, recipeId: RecipeArgument, block: CrafterRecipeCrafted.() -> Unit = {}) {
	criteria += CrafterRecipeCrafted(name, recipeId = recipeId).apply(block)
}

fun CrafterRecipeCrafted.ingredient(block: ItemStack.() -> Unit) {
	ingredients = (ingredients ?: emptyList()) + ItemStack().apply(block)
}
