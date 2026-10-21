package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStackPredicate
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Triggered when a crafter crafts one of the [recipes]. Recipe tags aren't possible because the game doesn't load tags for recipes.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#crafterrecipecrafted
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class CrafterRecipeCrafted(
	override var player: EntityOrPredicates? = null,
	var ingredients: List<ItemStackPredicate>? = null,
	var recipes: InlinableList<RecipeArgument>,
) : AdvancementTriggerCondition()

/** Add a `crafterRecipeCrafted` criterion, triggered when a crafter crafts one of [recipes]. */
fun AdvancementCriteria.crafterRecipeCrafted(name: String, vararg recipes: RecipeArgument, block: CrafterRecipeCrafted.() -> Unit = {}) {
	criteria[name] = CrafterRecipeCrafted(recipes = recipes.toList()).apply(block)
}

/** Add an ingredient to the recipe. */
fun CrafterRecipeCrafted.ingredient(block: ItemStackPredicate.() -> Unit) {
	ingredients = (ingredients ?: emptyList()) + ItemStackPredicate().apply(block)
}
