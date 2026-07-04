package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.generated.arguments.types.DimensionArgument
import kotlinx.serialization.Serializable

/**
 * Triggered when a player changes dimension.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#changeddimension
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class ChangedDimension(
	override var player: EntityOrPredicates? = null,
	var from: DimensionArgument? = null,
	var to: DimensionArgument? = null,
) : AdvancementTriggerCondition()

/** Add a `changedDimension` criterion, triggered when a player changes dimension. */
fun AdvancementCriteria.changedDimension(name: String, block: ChangedDimension.() -> Unit = {}) {
	criteria[name] = ChangedDimension().apply(block)
}
