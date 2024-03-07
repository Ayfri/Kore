package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = Component.Companion.ComponentSerializer::class)
sealed class Component {
	companion object {
		data object ComponentSerializer : NamespacedPolymorphicSerializer<Component>(Component::class)
	}
}
