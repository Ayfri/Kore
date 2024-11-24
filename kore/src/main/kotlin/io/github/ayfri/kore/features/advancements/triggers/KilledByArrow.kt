package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import kotlinx.serialization.Serializable

@Serializable
data class KilledByArrow(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var firedFromWeapon: ItemStack? = null,
	var uniqueEntityTypes: IntRangeOrIntJson? = null,
	var victims: List<EntityOrPredicates>? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.killedByArrow(name: String, block: KilledByArrow.() -> Unit = {}) {
	criteria += KilledByArrow(name).apply(block)
}

fun KilledByArrow.firedFromWeapon(block: ItemStack.() -> Unit) {
	firedFromWeapon = ItemStack().apply(block)
}

fun KilledByArrow.uniqueEntityTypes(block: IntRangeOrIntJson.() -> Unit) {
	uniqueEntityTypes = IntRangeOrIntJson().apply(block)
}

fun KilledByArrow.victim(block: EntityOrPredicates.() -> Unit) {
	victims = (victims ?: emptyList()) + EntityOrPredicates().apply(block)
}

fun KilledByArrow.victims(block: MutableList<EntityOrPredicates>.() -> Unit) {
	victims = mutableListOf<EntityOrPredicates>().apply(block)
}
