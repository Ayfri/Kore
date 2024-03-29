package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.types.resources.EffectArgument
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer

@Serializable
data class SuspiciousStewEffect(
	var id: EffectArgument,
	var duration: Int,
)

@Serializable(with = SuspiciousStewComponent.Companion.SuspiciousStewComponentSerializer::class)
data class SuspiciousStewComponent(
	var effects: List<SuspiciousStewEffect>,
) : Component() {
	companion object {
		object SuspiciousStewComponentSerializer : InlineSerializer<SuspiciousStewComponent, List<SuspiciousStewEffect>>(
			ListSerializer(SuspiciousStewEffect.serializer()),
			SuspiciousStewComponent::effects
		)
	}
}

fun Components.suspiciousStewComponent(effects: List<SuspiciousStewEffect>) = apply {
	components["suspicious_stew"] = SuspiciousStewComponent(effects)
}

fun Components.suspiciousStewComponent(vararg effects: SuspiciousStewEffect) = apply {
	components["suspicious_stew"] = SuspiciousStewComponent(effects.toList())
}

fun Components.suspiciousStewComponent(block: SuspiciousStewComponent.() -> Unit) = apply {
	components["suspicious_stew"] = SuspiciousStewComponent(mutableListOf()).apply(block)
}

fun SuspiciousStewComponent.effect(id: EffectArgument, duration: Int) = apply {
	effects += SuspiciousStewEffect(id, duration)
}
