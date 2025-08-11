package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

/**
 * Triggered when an entity is summoned.
 *
 * Docs: https://kore.ayfri.com/docs/advancements/triggers#summonedentity
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class SummonedEntity(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var entity: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

/** Add a `summonedEntity` criterion, triggered when an entity is summoned. */
fun AdvancementCriteria.summonedEntity(name: String, block: SummonedEntity.() -> Unit = {}) {
	criteria += SummonedEntity(name).apply(block)
}

/** Set the summoned entity constraints. */
fun SummonedEntity.entity(block: EntityOrPredicates.() -> Unit) {
	entity = EntityOrPredicates().apply(block)
}
