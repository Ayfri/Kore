package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.advancements.serializers.FloatRangeOrFloatJson
import kotlinx.serialization.Serializable

@Serializable
data class UsedEnderEye(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var distance: FloatRangeOrFloatJson? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.usedEnderEye(name: String, block: UsedEnderEye.() -> Unit = {}) {
	criteria += UsedEnderEye(name).apply(block)
}

fun UsedEnderEye.distance(block: FloatRangeOrFloatJson.() -> Unit) {
	distance = FloatRangeOrFloatJson().apply(block)
}
