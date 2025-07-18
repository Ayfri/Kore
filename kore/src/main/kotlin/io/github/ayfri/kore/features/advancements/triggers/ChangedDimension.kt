package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.generated.arguments.types.DimensionArgument
import kotlinx.serialization.Serializable

@Serializable
data class ChangedDimension(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var from: DimensionArgument? = null,
	var to: DimensionArgument? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.changedDimension(name: String, block: ChangedDimension.() -> Unit = {}) {
	criteria += ChangedDimension(name).apply(block)
}
