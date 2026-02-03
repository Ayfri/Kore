package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.arguments.numbers.ranges.serializers.FloatRangeOrFloatJson
import kotlinx.serialization.Serializable

/**
 * Triggered when an ender eye is used.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#usedendereye
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class UsedEnderEye(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var distance: FloatRangeOrFloatJson? = null,
) : AdvancementTriggerCondition()

/** Add a `usedEnderEye` criterion, triggered when an ender eye is used. */
fun AdvancementCriteria.usedEnderEye(name: String, block: UsedEnderEye.() -> Unit = {}) {
	criteria += UsedEnderEye(name).apply(block)
}

/** Set the distance constraints. */
fun UsedEnderEye.distance(block: FloatRangeOrFloatJson.() -> Unit) {
	distance = FloatRangeOrFloatJson().apply(block)
}
