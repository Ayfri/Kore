package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.arguments.types.resources.RecipeArgument
import kotlinx.serialization.Serializable

@Serializable
data class RecipeUnlocked(
	var recipe: RecipeArgument,
) : AdvancementTriggerCondition
