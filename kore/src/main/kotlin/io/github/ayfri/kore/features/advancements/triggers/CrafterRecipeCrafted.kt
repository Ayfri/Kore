package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument
import kotlinx.serialization.Serializable

/**
 * Triggered when a crafter crafts a recipe.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#crafterrecipecrafted
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class CrafterRecipeCrafted(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var ingredients: List<ItemStack>? = null,
	var recipeId: RecipeArgument,
) : AdvancementTriggerCondition()

/** Add a `crafterRecipeCrafted` criterion, triggered when a crafter crafts a recipe. */
fun AdvancementCriteria.crafterRecipeCrafted(name: String, recipeId: RecipeArgument, block: CrafterRecipeCrafted.() -> Unit = {}) {
	criteria += CrafterRecipeCrafted(name, recipeId = recipeId).apply(block)
}

/** Add an ingredient to the recipe. */
fun CrafterRecipeCrafted.ingredient(block: ItemStack.() -> Unit) {
	ingredients = (ingredients ?: emptyList()) + ItemStack().apply(block)
}
