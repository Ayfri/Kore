package io.github.ayfri.kore.arguments.actions

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable
sealed interface ActionType

@Serializable(with = Action.Companion.ActionSerializer::class)
sealed class Action : ActionType {
	companion object {
		data object ActionSerializer : NamespacedPolymorphicSerializer<Action>(Action::class)
	}
}

sealed class ActionWrapper<out T : ActionType> {
	abstract var action: @UnsafeVariance T?
}
