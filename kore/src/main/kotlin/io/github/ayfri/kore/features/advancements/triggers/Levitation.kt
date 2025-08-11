package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.features.predicates.sub.Distance
import kotlinx.serialization.Serializable

/**
 * Triggered when the player levitates.
 *
 * Docs: https://kore.ayfri.com/docs/advancements/triggers#levitation
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class Levitation(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var distance: Distance? = null,
	var duration: IntRangeOrIntJson? = null,
) : AdvancementTriggerCondition()

/** Add a `levitation` criterion, triggered when the player levitates. */
fun AdvancementCriteria.levitation(name: String, block: Levitation.() -> Unit = {}) {
	criteria += Levitation(name).apply(block)
}

/** Set the distance constraints. */
fun Levitation.distance(block: Distance.() -> Unit) {
	distance = Distance().apply(block)
}
