package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.types.resources.EffectArgument
import io.github.ayfri.kore.generated.ComponentTypes
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer

@Serializable
data class SuspiciousStewEffect(
	var id: EffectArgument,
	var duration: Int,
)

@Serializable(with = SuspiciousStewEffectsComponent.Companion.SuspiciousStewEffectsComponentSerializer::class)
data class SuspiciousStewEffectsComponent(
	var effects: List<SuspiciousStewEffect>,
) : Component() {
	companion object {
		object SuspiciousStewEffectsComponentSerializer : InlineSerializer<SuspiciousStewEffectsComponent, List<SuspiciousStewEffect>>(
			ListSerializer(SuspiciousStewEffect.serializer()),
			SuspiciousStewEffectsComponent::effects
		)
	}
}

fun Components.suspiciousStewEffectsComponent(effects: List<SuspiciousStewEffect>) = apply {
	this[ComponentTypes.SUSPICIOUS_STEW_EFFECTS] = SuspiciousStewEffectsComponent(effects)
}

fun Components.suspiciousStewEffectsComponent(vararg effects: SuspiciousStewEffect) = apply {
	this[ComponentTypes.SUSPICIOUS_STEW_EFFECTS] = SuspiciousStewEffectsComponent(effects.toList())
}

fun Components.suspiciousStewEffectsComponent(block: SuspiciousStewEffectsComponent.() -> Unit) = apply {
	this[ComponentTypes.SUSPICIOUS_STEW_EFFECTS] = SuspiciousStewEffectsComponent(mutableListOf()).apply(block)
}

fun SuspiciousStewEffectsComponent.effect(id: EffectArgument, duration: Int) = apply {
	effects += SuspiciousStewEffect(id, duration)
}
