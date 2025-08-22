package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

/**
 * Triggered when a beacon is constructed.
 *
 * Docs: https://kore.ayfri.com/docs/advancements/triggers#constructbeacon
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class ConstructBeacon(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var level: IntRangeOrIntJson? = null,
) : AdvancementTriggerCondition()

/** Add a `constructBeacon` criterion, triggered when a beacon is constructed. */
fun AdvancementCriteria.constructBeacon(name: String, block: ConstructBeacon.() -> Unit = {}) {
	criteria += ConstructBeacon(name).apply(block)
}
