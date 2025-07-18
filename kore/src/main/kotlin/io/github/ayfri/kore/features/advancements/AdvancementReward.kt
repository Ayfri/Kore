package io.github.ayfri.kore.features.advancements

import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.generated.arguments.types.LootTableArgument
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument
import kotlinx.serialization.Serializable

@Serializable
data class AdvancementReward(
	var experience: Int? = null,
	var function: FunctionArgument? = null,
	var loot: List<LootTableArgument>? = null,
	var recipes: List<RecipeArgument>? = null,
)

fun AdvancementReward.loots(vararg loot: LootTableArgument) {
	this.loot = loot.toList()
}

fun AdvancementReward.recipes(vararg recipe: RecipeArgument) {
	recipes = recipe.toList()
}
