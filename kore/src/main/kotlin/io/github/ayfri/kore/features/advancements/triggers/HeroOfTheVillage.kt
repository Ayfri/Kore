package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

@Serializable
data class HeroOfTheVillage(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.heroOfTheVillage(name: String, block: HeroOfTheVillage.() -> Unit = {}) {
	criteria += HeroOfTheVillage(name).apply(block)
}
