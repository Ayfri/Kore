package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

@Serializable(with = LockComponent.Companion.LockComponentSerializer::class)
data class LockComponent(var name: String) : Component() {
	companion object {
		object LockComponentSerializer : InlineSerializer<LockComponent, String>(
			String.serializer(),
			LockComponent::name
		)
	}
}

fun Components.lock(name: String) = apply {
	components["lock"] = LockComponent(name)
}
