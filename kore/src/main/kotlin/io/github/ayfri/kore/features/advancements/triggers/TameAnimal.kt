package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

/**
 * Triggered when an animal is tamed.
 *
 * Docs: https://kore.ayfri.com/docs/advancements/triggers#tameanimal
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class TameAnimal(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var entity: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

/** Add a `tameAnimal` criterion, triggered when an animal is tamed. */
fun AdvancementCriteria.tameAnimal(name: String, block: TameAnimal.() -> Unit = {}) {
	criteria += TameAnimal(name).apply(block)
}

/** Set the animal entity constraints. */
fun TameAnimal.entity(block: EntityOrPredicates.() -> Unit) {
	entity = EntityOrPredicates().apply(block)
}
