package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument
import kotlinx.serialization.Serializable

/**
 * Triggered when a recipe is crafted.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#recipecrafted
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class RecipeCrafted(
	override var player: EntityOrPredicates? = null,
	var recipeId: RecipeArgument,
	var ingredients: List<ItemStack>? = null,
) : AdvancementTriggerCondition()

/** Add a `recipeCrafted` criterion, triggered when a recipe is crafted. */
fun AdvancementCriteria.recipeCrafted(name: String, recipeId: RecipeArgument, block: RecipeCrafted.() -> Unit) {
	criteria[name] = RecipeCrafted(recipeId = recipeId).apply(block)
}

/** Replace the ingredient list with a single constraint. */
fun RecipeCrafted.ingredients(block: ItemStack.() -> Unit) {
	ingredients = listOf(ItemStack().apply(block))
}

/** Append an ingredient constraint. */
fun RecipeCrafted.ingredient(block: ItemStack.() -> Unit) {
	ingredients = (ingredients ?: emptyList()) + listOf(ItemStack().apply(block))
}
