package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument
import kotlinx.serialization.Serializable

@Serializable
data class RecipeUnlocked(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var recipe: RecipeArgument,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.recipeUnlocked(name: String, recipe: RecipeArgument, block: RecipeUnlocked.() -> Unit) {
	criteria += RecipeUnlocked(name, recipe = recipe).apply(block)
}
