package io.github.ayfri.kore.features.advancements

import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.arguments.types.resources.LootTableArgument
import io.github.ayfri.kore.arguments.types.resources.RecipeArgument
import kotlinx.serialization.Serializable

@Serializable
data class AdvancementReward(
	var experience: Int? = null,
	var function: FunctionArgument? = null,
	var loot: List<LootTableArgument>? = null,
	var recipes: List<RecipeArgument>? = null,
)
