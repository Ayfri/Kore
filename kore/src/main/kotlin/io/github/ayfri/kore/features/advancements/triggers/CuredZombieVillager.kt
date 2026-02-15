package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

/**
 * Triggered when a zombie villager is cured.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#curedzombievillager
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class CuredZombieVillager(
	override var player: EntityOrPredicates? = null,
	var villager: EntityOrPredicates? = null,
	var zombie: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

/** Add a `curedZombieVillager` criterion, triggered when a zombie villager is cured. */
fun AdvancementCriteria.curedZombieVillager(name: String, block: CuredZombieVillager.() -> Unit = {}) {
	criteria[name] = CuredZombieVillager().apply(block)
}

/** Set the villager constraints. */
fun CuredZombieVillager.villager(block: EntityOrPredicates.() -> Unit) {
	villager = EntityOrPredicates().apply(block)
}

/** Set the zombie constraints. */
fun CuredZombieVillager.zombie(block: EntityOrPredicates.() -> Unit) {
	zombie = EntityOrPredicates().apply(block)
}
