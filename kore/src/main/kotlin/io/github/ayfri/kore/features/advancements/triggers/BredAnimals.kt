package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

/**
 * Triggered when animals are bred.
 *
 * Docs: https://kore.ayfri.com/docs/advancements/triggers#bredanimals
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class BredAnimals(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var child: EntityOrPredicates? = null,
	var parent: EntityOrPredicates? = null,
	var partner: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

/** Add a `bredAnimals` criterion, triggered when two animals breed. */
fun AdvancementCriteria.bredAnimals(name: String, block: BredAnimals.() -> Unit = {}) {
	criteria += BredAnimals(name).apply(block)
}

/** Set the child entity constraints. */
fun BredAnimals.child(block: EntityOrPredicates.() -> Unit) {
	child = EntityOrPredicates().apply(block)
}

/** Set one parent constraints. */
fun BredAnimals.parent(block: EntityOrPredicates.() -> Unit) {
	parent = EntityOrPredicates().apply(block)
}

/** Set the partner constraints. */
fun BredAnimals.partner(block: EntityOrPredicates.() -> Unit) {
	partner = EntityOrPredicates().apply(block)
}
