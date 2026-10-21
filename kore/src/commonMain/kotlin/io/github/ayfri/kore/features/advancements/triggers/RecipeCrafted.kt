package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStackPredicate
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Triggered when one of the [recipes] is crafted. Recipe tags aren't possible because the game doesn't load tags for recipes.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#recipecrafted
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class RecipeCrafted(
	override var player: EntityOrPredicates? = null,
	var ingredients: List<ItemStackPredicate>? = null,
	var recipes: InlinableList<RecipeArgument>,
) : AdvancementTriggerCondition()

/** Add a `recipeCrafted` criterion, triggered when one of [recipes] is crafted. */
fun AdvancementCriteria.recipeCrafted(name: String, vararg recipes: RecipeArgument, block: RecipeCrafted.() -> Unit = {}) {
	criteria[name] = RecipeCrafted(recipes = recipes.toList()).apply(block)
}

/** Replace the ingredient list with a single constraint. */
fun RecipeCrafted.ingredients(block: ItemStackPredicate.() -> Unit) {
	ingredients = listOf(ItemStackPredicate().apply(block))
}

/** Append an ingredient constraint. */
fun RecipeCrafted.ingredient(block: ItemStackPredicate.() -> Unit) {
	ingredients = (ingredients ?: emptyList()) + listOf(ItemStackPredicate().apply(block))
}
