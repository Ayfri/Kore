package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

@Serializable
data class BredAnimals(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var child: EntityOrPredicates? = null,
	var parent: EntityOrPredicates? = null,
	var partner: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.bredAnimals(name: String, block: BredAnimals.() -> Unit = {}) {
	criteria += BredAnimals(name).apply(block)
}

fun BredAnimals.child(block: EntityOrPredicates.() -> Unit) {
	child = EntityOrPredicates().apply(block)
}

fun BredAnimals.parent(block: EntityOrPredicates.() -> Unit) {
	parent = EntityOrPredicates().apply(block)
}

fun BredAnimals.partner(block: EntityOrPredicates.() -> Unit) {
	partner = EntityOrPredicates().apply(block)
}
