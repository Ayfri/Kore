package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.MobEffectArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable
data class SuspiciousStewEffect(
	var id: MobEffectArgument,
	var duration: Int,
)

@Serializable(with = SuspiciousStewEffectsComponent.Companion.SuspiciousStewEffectsComponentSerializer::class)
data class SuspiciousStewEffectsComponent(var effects: List<SuspiciousStewEffect>) : Component() {
	companion object {
		data object SuspiciousStewEffectsComponentSerializer : InlineAutoSerializer<SuspiciousStewEffectsComponent>(
			SuspiciousStewEffectsComponent::class
		)
	}
}

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
