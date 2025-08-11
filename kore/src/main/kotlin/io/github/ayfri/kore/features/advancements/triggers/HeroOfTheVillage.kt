package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

/**
 * Triggered when a player is the hero of the village.
 *
 * Docs: https://kore.ayfri.com/docs/advancements/triggers#heroofthevillage
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class HeroOfTheVillage(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

/** Add a `heroOfTheVillage` criterion, triggered when a player is the hero of the village. */
fun AdvancementCriteria.heroOfTheVillage(name: String, block: HeroOfTheVillage.() -> Unit = {}) {
	criteria += HeroOfTheVillage(name).apply(block)
}
