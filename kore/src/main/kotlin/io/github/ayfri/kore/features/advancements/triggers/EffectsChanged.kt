package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.Effect
import io.github.ayfri.kore.generated.Effects
import kotlinx.serialization.Serializable

/**
 * Triggered when a player's effects change.
 *
 * Docs: https://kore.ayfri.com/docs/advancements/triggers#effectschanged
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class EffectsChanged(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var effects: Map<Effects, Effect>? = null,
	var source: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

/** Add an `effectsChanged` criterion, triggered when a player's effects change. */
fun AdvancementCriteria.effectsChanged(name: String, block: EffectsChanged.() -> Unit = {}) {
	criteria += EffectsChanged(name).apply(block)
}

/** Set the effects map constraints. */
fun EffectsChanged.effects(block: Map<Effects, Effect>.() -> Unit) {
	effects = buildMap(block)
}

/** Add or replace a single effect constraint. */
fun EffectsChanged.effect(effect: Effects, block: Effect.() -> Unit) {
	effects = (effects?.toMutableMap() ?: mutableMapOf()).apply { this[effect] = Effect().apply(block) }
}

/** Set the source entity constraints. */
fun EffectsChanged.source(block: EntityOrPredicates.() -> Unit) {
	source = EntityOrPredicates().apply(block)
}
