package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.generated.arguments.types.PotionArgument
import kotlinx.serialization.Serializable

/**
 * Triggered when a potion is brewed.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#brewedpotion
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class BrewedPotion(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var potion: PotionArgument? = null,
) : AdvancementTriggerCondition()

/** Add a `brewedPotion` criterion, triggered when a potion is brewed. */
fun AdvancementCriteria.brewedPotion(name: String, block: BrewedPotion.() -> Unit = {}) {
	criteria += BrewedPotion(name).apply(block)
}
