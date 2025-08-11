package io.github.ayfri.kore.features.advancements

import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.generated.arguments.types.LootTableArgument
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument
import kotlinx.serialization.Serializable

/**
 * Rewards granted when an advancement is completed.
 *
 * Docs: https://kore.ayfri.com/docs/advancements#rewards
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement#JSON_format
 */
@Serializable
data class AdvancementReward(
	var experience: Int? = null,
	var function: FunctionArgument? = null,
	var loot: List<LootTableArgument>? = null,
	var recipes: List<RecipeArgument>? = null,
)

/** Set the loot table rewards. */
fun AdvancementReward.loots(vararg loot: LootTableArgument) {
	this.loot = loot.toList()
}

/** Set the recipe rewards. */
fun AdvancementReward.recipes(vararg recipe: RecipeArgument) {
	recipes = recipe.toList()
}
