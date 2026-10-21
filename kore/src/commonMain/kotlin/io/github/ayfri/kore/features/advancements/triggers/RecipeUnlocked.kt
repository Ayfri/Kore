package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Triggered when one of the [recipes] is unlocked. Recipe tags aren't possible because the game doesn't load tags for recipes.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#recipeunlocked
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class RecipeUnlocked(
	override var player: EntityOrPredicates? = null,
	var recipes: InlinableList<RecipeArgument>,
) : AdvancementTriggerCondition()

/** Add a `recipeUnlocked` criterion, triggered when one of [recipes] is unlocked. */
fun AdvancementCriteria.recipeUnlocked(name: String, vararg recipes: RecipeArgument, block: RecipeUnlocked.() -> Unit = {}) {
	criteria[name] = RecipeUnlocked(recipes = recipes.toList()).apply(block)
}
