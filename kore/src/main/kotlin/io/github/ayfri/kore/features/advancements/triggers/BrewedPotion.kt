package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.arguments.types.resources.PotionArgument
import kotlinx.serialization.Serializable

@Serializable
data class BrewedPotion(
	var potion: PotionArgument? = null,
) : AdvancementTriggerCondition
