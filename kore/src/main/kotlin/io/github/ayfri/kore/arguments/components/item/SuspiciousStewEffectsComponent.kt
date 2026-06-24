package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.MobEffectArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/** A single effect entry (id + duration in ticks) applied when eating a suspicious stew. */
@Serializable
data class SuspiciousStewEffect(
	var id: MobEffectArgument,
	var duration: Int,
)

/**
 * Represents the `minecraft:suspicious_stew_effects` item component, which sets the effects applied when consuming a suspicious stew.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#suspicious_stew_effects
 */
@Serializable(with = SuspiciousStewEffectsComponent.Companion.SuspiciousStewEffectsComponentSerializer::class)
data class SuspiciousStewEffectsComponent(var effects: List<SuspiciousStewEffect>) : Component() {
	companion object {
		data object SuspiciousStewEffectsComponentSerializer : InlineAutoSerializer<SuspiciousStewEffectsComponent>(
			SuspiciousStewEffectsComponent::class
		)
	}
}

/** Sets the effects applied when consuming a suspicious stew. */
fun ComponentsScope.suspiciousStewEffectsComponent(effects: List<SuspiciousStewEffect>) = apply {
	this[ItemComponentTypes.SUSPICIOUS_STEW_EFFECTS] = SuspiciousStewEffectsComponent(effects)
}

fun ComponentsScope.suspiciousStewEffectsComponent(vararg effects: SuspiciousStewEffect) = apply {
	this[ItemComponentTypes.SUSPICIOUS_STEW_EFFECTS] = SuspiciousStewEffectsComponent(effects.toList())
}

fun ComponentsScope.suspiciousStewEffectsComponent(block: SuspiciousStewEffectsComponent.() -> Unit) = apply {
	this[ItemComponentTypes.SUSPICIOUS_STEW_EFFECTS] = SuspiciousStewEffectsComponent(mutableListOf()).apply(block)
}

fun SuspiciousStewEffectsComponent.effect(id: MobEffectArgument, duration: Int) = apply {
	effects += SuspiciousStewEffect(id, duration)
}
