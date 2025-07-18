package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.generated.arguments.types.PotionArgument
import kotlinx.serialization.Serializable

@Serializable
data class BrewedPotion(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var potion: PotionArgument? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.brewedPotion(name: String, block: BrewedPotion.() -> Unit = {}) {
	criteria += BrewedPotion(name).apply(block)
}
