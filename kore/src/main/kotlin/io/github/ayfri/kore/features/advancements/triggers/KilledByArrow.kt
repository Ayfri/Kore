package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import kotlinx.serialization.Serializable

/**
 * Triggered when a player is killed by an arrow.
 *
 * Docs: https://kore.ayfri.com/docs/advancements/triggers#killedbyarrow
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class KilledByArrow(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var firedFromWeapon: ItemStack? = null,
	var uniqueEntityTypes: IntRangeOrIntJson? = null,
	var victims: List<EntityOrPredicates>? = null,
) : AdvancementTriggerCondition()

/** Add a `killedByArrow` criterion, triggered when a player is killed by an arrow. */
fun AdvancementCriteria.killedByArrow(name: String, block: KilledByArrow.() -> Unit = {}) {
	criteria += KilledByArrow(name).apply(block)
}

/** Set the weapon constraints. */
fun KilledByArrow.firedFromWeapon(block: ItemStack.() -> Unit) {
	firedFromWeapon = ItemStack().apply(block)
}

/** Set the unique entity types constraint. */
fun KilledByArrow.uniqueEntityTypes(block: IntRangeOrIntJson.() -> Unit) {
	uniqueEntityTypes = IntRangeOrIntJson().apply(block)
}

/** Add one victim constraint. */
fun KilledByArrow.victim(block: EntityOrPredicates.() -> Unit) {
	victims = (victims ?: emptyList()) + EntityOrPredicates().apply(block)
}

/** Replace the victim constraints list. */
fun KilledByArrow.victims(block: MutableList<EntityOrPredicates>.() -> Unit) {
	victims = mutableListOf<EntityOrPredicates>().apply(block)
}
