package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

/**
 * Triggered when a player is the hero of the village.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#heroofthevillage
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class HeroOfTheVillage(
	override var player: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

/** Add a `heroOfTheVillage` criterion, triggered when a player is the hero of the village. */
fun AdvancementCriteria.heroOfTheVillage(name: String, block: HeroOfTheVillage.() -> Unit = {}) {
	criteria[name] = HeroOfTheVillage().apply(block)
}
